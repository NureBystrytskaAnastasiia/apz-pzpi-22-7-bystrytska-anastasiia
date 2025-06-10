const express = require('express');
const router = express.Router();
const orderController = require('../controllers/order.controller');

router.post('/', orderController.createOrder);
router.get('/', orderController.getOrders);
router.post('/:id/complete', orderController.completeOrder);
router.get('/:id', orderController.getOrderById);
router.post('/:id/invoice', orderController.generateInvoice);

module.exports = router;