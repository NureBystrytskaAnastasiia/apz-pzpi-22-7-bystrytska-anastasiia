const Medicine = require('../models/medicine.model');
const mongoose = require('mongoose');
const Sale = require('../models/sale.model');

const checkExpiration = (medicine) => {
  const now = new Date();
  const diffDays = (new Date(medicine.expirationDate) - now) / (1000 * 60 * 60 * 24);
  return diffDays <= 7;
};

const checkStorageConditions = (medicine, currentTemp, currentHumidity) => {
  // Отримуємо діапазон температур з ліків
  const tempMin = medicine.temperatureRange?.min || -Infinity;
  const tempMax = medicine.temperatureRange?.max || Infinity;
  
  // Отримуємо діапазон вологості з ліків
  const humMin = medicine.humidityRange?.min || 0;
  const humMax = medicine.humidityRange?.max || 100;
  
  // Перевіряємо, чи поточні умови відповідають вимогам
  const isTempValid = currentTemp >= tempMin && currentTemp <= tempMax;
  const isHumidityValid = currentHumidity >= humMin && currentHumidity <= humMax;
  
  return {
    isTempValid,
    isHumidityValid
  };
};

exports.createMedicine = async (req, res) => {
  try {
    const { 
      name, 
      quantity, 
      expirationDate, 
      supplier, 
      price, 
      storageRoomId,
      temperatureRange,
      humidityRange,
      storageInstructions
    } = req.body;
    
    const medicine = await Medicine.create({ 
      name, 
      quantity, 
      expirationDate, 
      supplier, 
      price, 
      storageRoomId,
      temperatureRange,
      humidityRange,
      storageInstructions
    });
    
    res.status(201).json({ 
      ...medicine.toObject(), 
      isExpiringSoon: checkExpiration(medicine)
    });
  } catch (error) {
    res.status(400).json({ error: 'Invalid data', details: error.message });
  }
};

exports.getAllMedicines = async (req, res) => {
  try {
    const medicines = await Medicine.find().populate('storageRoomId', 'name temperature humidity');
    const results = medicines.map(m => {
      const roomTemp = m.storageRoomId?.temperature || 0;
      const roomHumidity = m.storageRoomId?.humidity || 0;
      
      const roomConditions = checkStorageConditions(m, roomTemp, roomHumidity);
      
      return { 
        ...m.toObject(), 
        isExpiringSoon: checkExpiration(m),
        storageConditions: roomConditions,
        storageStatus: roomConditions.isTempValid && roomConditions.isHumidityValid ? 
          'Оптимальні умови' : 'Поза межами норми'
      };
    });
    res.json(results);
  } catch (error) {
    res.status(500).json({ error: 'Failed to retrieve medicines', details: error.message });
  }
};

exports.getMedicineById = async (req, res) => {
  try {
    const { id } = req.params;
    if (!mongoose.Types.ObjectId.isValid(id)) throw new Error('Invalid ID');
    
    const medicine = await Medicine.findById(id).populate('storageRoomId', 'name temperature humidity');
    if (!medicine) return res.status(404).json({ error: 'Medicine not found' });
    
    const roomConditions = medicine.storageRoomId ? 
      checkStorageConditions(medicine, medicine.storageRoomId.temperature, medicine.storageRoomId.humidity) : 
      { isTempValid: false, isHumidityValid: false };
    
    res.json({ 
      ...medicine.toObject(), 
      isExpiringSoon: checkExpiration(medicine),
      storageConditions: roomConditions,
      storageStatus: roomConditions.isTempValid && roomConditions.isHumidityValid ? 
        'Оптимальні умови' : 'Поза межами норми'
    });
  } catch (error) {
    res.status(400).json({ error: 'Invalid request', details: error.message });
  }
};

exports.updateMedicine = async (req, res) => {
  try {
    const { id } = req.params;
    const updates = req.body;
    
    if (!mongoose.Types.ObjectId.isValid(id)) throw new Error('Invalid ID');
    
    const medicine = await Medicine.findByIdAndUpdate(id, updates, { new: true })
      .populate('storageRoomId', 'name temperature humidity');
    
    if (!medicine) return res.status(404).json({ error: 'Medicine not found' });
    
    const roomConditions = medicine.storageRoomId ? 
      checkStorageConditions(medicine, medicine.storageRoomId.temperature, medicine.storageRoomId.humidity) : 
      { isTempValid: false, isHumidityValid: false };
    
    res.json({ 
      ...medicine.toObject(), 
      isExpiringSoon: checkExpiration(medicine),
      storageConditions: roomConditions,
      storageStatus: roomConditions.isTempValid && roomConditions.isHumidityValid ? 
        'Оптимальні умови' : 'Поза межами норми'
    });
  } catch (error) {
    res.status(400).json({ error: 'Failed to update medicine', details: error.message });
  }
};

exports.deleteMedicine = async (req, res) => {
  try {
    const { id } = req.params;
    if (!mongoose.Types.ObjectId.isValid(id)) throw new Error('Invalid ID');
    const result = await Medicine.findByIdAndDelete(id);
    if (!result) return res.status(404).json({ error: 'Medicine not found' });
    res.json({ message: 'Medicine deleted successfully' });
  } catch (error) {
    res.status(400).json({ error: 'Failed to delete medicine', details: error.message });
  }
};
exports.editeMedicine = async (req, res) => {
  try {
    const { quantity } = req.body;
    const medicine = await Medicine.findByIdAndUpdate(
      req.params.id,
      { quantity },
      { new: true }
    );
    res.json(medicine);
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
};
exports.simulateSale = async (req, res) => {
  try {
    const medicines = await Medicine.find({ quantity: { $gt: 0 } });
    
    if (!medicines.length) {
      return res.status(404).json({ error: 'No medicines available for sale' });
    }

    const randomIndex = Math.floor(Math.random() * medicines.length);
    const medicine = medicines[randomIndex];
    const saleQty = Math.floor(Math.random() * 3) + 1;
    
    if (medicine.quantity < saleQty) {
      return res.json({ 
        message: 'Not enough quantity to simulate sale',
        medicine: medicine.name,
        available: medicine.quantity
      });
    }

    // Оновлюємо кількість
    medicine.quantity -= saleQty;
    await medicine.save();

    // Створюємо запис про продаж
    const sale = await Sale.create({
      medicine: medicine._id,
      quantity: saleQty,
      price: medicine.price
    });

    res.json({
      message: 'Sale simulated successfully',
      medicine: medicine.name,
      sold: saleQty,
      remaining: medicine.quantity,
      price: medicine.price,
      total: (saleQty * medicine.price).toFixed(2),
      isExpiringSoon: checkExpiration(medicine)
    });
  } catch (error) {
    res.status(400).json({ 
      error: 'Failed to simulate sale',
      details: error.message 
    });
  }
};
// У medicine.controller.js
// medicine.controller.js
exports.getStats = async (req, res) => {
  try {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 1);
    
    const weekFromNow = new Date();
    weekFromNow.setDate(weekFromNow.getDate() + 7);
    
    // 1. Загальна кількість препаратів
    const totalMedicines = await Medicine.countDocuments();
    
    // 2. Ліки, які закінчуються (кількість < 10)
    const lowStock = await Medicine.countDocuments({ quantity: { $lt: 10 } });
    
    // 3. Ліки з критично малою кількістю (<5)
    const criticalStock = await Medicine.countDocuments({ quantity: { $lt: 5 } });
    
    // 4. Ліки з терміном придатності, що закінчується (менше 7 днів)
    const expiringSoon = await Medicine.countDocuments({ 
      expirationDate: { $lte: weekFromNow }
    });
    
    // 5. Продажі за сьогодні
    const todaySales = await Sale.countDocuments({ 
      date: { $gte: today, $lt: tomorrow } 
    });
    
    // 6. Дохід за сьогодні
    const todayRevenueResult = await Sale.aggregate([
      { 
        $match: { 
          date: { $gte: today, $lt: tomorrow } 
        } 
      },
      { 
        $group: { 
          _id: null, 
          total: { $sum: { $multiply: ["$quantity", "$price"] } } 
        } 
      }
    ]);
    const todayRevenue = todayRevenueResult[0]?.total || 0;
    
    // 7. ВИПРАВЛЕНІ дані для графіка (останні 7 днів)
    const sevenDaysAgo = new Date();
    sevenDaysAgo.setDate(sevenDaysAgo.getDate() - 6); // Починаємо з 6 днів тому (разом з сьогодні = 7 днів)
    sevenDaysAgo.setHours(0, 0, 0, 0);
    
    console.log('Date range for sales data:', {
      from: sevenDaysAgo,
      to: tomorrow
    });
    
    // Спочатку отримуємо всі продажі за останні 7 днів
    const allSales = await Sale.find({
      date: { $gte: sevenDaysAgo, $lt: tomorrow }
    }).sort({ date: 1 });
    
    console.log('Found sales:', allSales.length);
    
    // Групуємо продажі по датах
    const salesByDate = {};
    
    // Ініціалізуємо всі дні нулями
    for (let i = 0; i < 7; i++) {
      const date = new Date(sevenDaysAgo);
      date.setDate(date.getDate() + i);
      const dateKey = date.toISOString().split('T')[0]; // YYYY-MM-DD формат
      salesByDate[dateKey] = {
        sales: 0,
        revenue: 0
      };
    }
    
    // Заповнюємо реальними даними
    allSales.forEach(sale => {
      const dateKey = sale.date.toISOString().split('T')[0];
      if (salesByDate[dateKey]) {
        salesByDate[dateKey].sales += sale.quantity;
        salesByDate[dateKey].revenue += (sale.quantity * sale.price);
      }
    });
    
    // Конвертуємо в масив для графіка
    const salesData = Object.keys(salesByDate)
      .sort()
      .map(dateKey => ({
        date: new Date(dateKey).toLocaleDateString('uk-UA', {
          day: '2-digit',
          month: '2-digit'
        }),
        sales: salesByDate[dateKey].sales,
        revenue: Math.round(salesByDate[dateKey].revenue * 100) / 100 // Округлюємо до 2 знаків
      }));
    
    console.log('Processed sales data:', salesData);
    
    const response = {
      totalMedicines,
      lowStock,
      criticalStock,
      expiringSoon,
      todaySales,
      todayRevenue: Math.round(todayRevenue * 100) / 100,
      salesData: salesData
    };
    
    console.log('Sending response:', response);
    res.json(response);
    
  } catch (error) {
    console.error('Error in getStats:', error);
    res.status(500).json({ 
      error: 'Failed to get statistics',
      details: error.message 
    });
  }
};