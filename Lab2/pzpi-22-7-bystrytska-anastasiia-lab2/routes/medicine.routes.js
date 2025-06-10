    const express = require('express');
    const router = express.Router();
    const medicineController = require('../controllers/medicine.controller');

    router.post('/medicines', medicineController.createMedicine);
    router.get('/medicines', medicineController.getAllMedicines);
    router.get('/medicines/stats', medicineController.getStats);
    router.get('/medicines/:id', medicineController.getMedicineById);
    router.put('/medicines/:id', medicineController.updateMedicine);
    router.delete('/medicines/:id', medicineController.deleteMedicine);
    router.patch('/medicines/:id', medicineController.editeMedicine);
    router.post('/medicines/simulate-sale', medicineController.simulateSale);

    module.exports = router;