const express = require('express');
const router = express.Router();
const userController = require('../controllers/user.controller');

// Тільки адміни можуть керувати користувачами — тимчасово без перевірки прав
router.get('/users', userController.getAllUsers);
router.delete('/users/:id', userController.deleteUser);

module.exports = router;
