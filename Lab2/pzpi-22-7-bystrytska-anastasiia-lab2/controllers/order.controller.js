const Order = require('../models/order.model');
const Medicine = require('../models/medicine.model');
const { generateInvoice } = require('../services/invoiceService');


exports.createOrder = async (req, res) => {
  try {
    const { items } = req.body;
    
    if (!items || !Array.isArray(items)) {
      return res.status(400).json({ error: 'Items must be an array' });
    }
    

    // Перевіряємо наявність ліків і збираємо дані
    const orderItems = [];
    let totalAmount = 0;

    for (const item of items) {
      const medicine = await Medicine.findById(item.medicineId);
      if (!medicine) {
        return res.status(404).json({ error: `Medicine with ID ${item.medicineId} not found` });
      }

      const unitPrice = medicine.price;
      const totalPrice = unitPrice * item.quantity;

      orderItems.push({
        medicineId: medicine._id,
        medicineName: medicine.name,
        supplier: medicine.supplier,
        quantity: item.quantity,
        unitPrice: unitPrice,
        totalPrice: totalPrice
      });

      totalAmount += totalPrice;
    }

    const order = await Order.create({
      items: orderItems,
      totalAmount: totalAmount,
      status: 'created'
    });

    // Симуляція доставки через 1 хвилину
    setTimeout(async () => {
      const invoice = await generateInvoice(order);
      
      await Order.findByIdAndUpdate(order._id, {
        status: 'delivered',
        deliveredAt: new Date(),
        invoiceNumber: `INV-${Date.now()}`,
        invoiceData: invoice
      });
      
      console.log(`Order ${order._id} delivered with invoice`);
    }, 60000); // 1 хвилина

    res.status(201).json(order);
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
};

exports.completeOrder = async (req, res) => {
  try {
    const { id } = req.params;
    
    const order = await Order.findById(id);
    if (!order) {
      return res.status(404).json({ error: 'Order not found' });
    }

    if (order.status !== 'delivered') {
      return res.status(400).json({ error: 'Order is not ready for completion' });
    }

    // Обробляємо кожен товар у замовленні
    for (const item of order.items) {
      // Оновлюємо кількість ліків для кожного товару
      await Medicine.findByIdAndUpdate(item.medicineId, {
        $inc: { quantity: item.quantity }
      });
    }

    // Оновлюємо статус замовлення
    const updatedOrder = await Order.findByIdAndUpdate(id, {
      status: 'completed',
      completedAt: new Date()
    }, { new: true });

    res.json(updatedOrder);
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
};

// Додайте цей метод до order.controller.js
exports.getOrderById = async (req, res) => {
  try {
    const order = await Order.findById(req.params.id);
    if (!order) {
      return res.status(404).json({ error: 'Order not found' });
    }
    res.json(order);
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
};

exports.getOrders = async (req, res) => {
  try {
    const orders = await Order.find().sort({ createdAt: -1 });
    res.json(orders);
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
};
exports.generateInvoice = async (req, res) => {
  try {
      const order = await Order.findById(req.params.id);
      if (!order) {
          return res.status(404).json({ error: 'Order not found' });
      }

      // Якщо накладна вже є - повертаємо її
      if (order.invoiceData) {
          return res.json({
              pdf: order.invoiceData.pdf,
              invoiceNumber: order.invoiceNumber
          });
      }

      // Генеруємо нову накладну
      const invoice = await generateInvoice(order);
      
      const updatedOrder = await Order.findByIdAndUpdate(order._id, {
          invoiceNumber: `INV-${Date.now()}`,
          invoiceData: invoice
      }, { new: true });

      res.json({
          pdf: invoice.pdf,
          invoiceNumber: updatedOrder.invoiceNumber
      });
  } catch (error) {
      res.status(400).json({ error: error.message });
  }
};