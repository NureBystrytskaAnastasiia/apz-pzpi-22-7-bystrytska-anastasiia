const express = require('express');
const router = express.Router();
const { register, login, forgotPassword, logout } = require('../controllers/authController');
const { isAuthenticated } = require('../middlewares/authMiddleware');

// Реєстрація
router.post('/register', register);

// Вхід
router.post('/login', login);

// Відновлення паролю
router.post('/forgot-password', forgotPassword);

// Вихід
router.post('/logout', logout);

// Профіль (доступний лише авторизованим користувачам)
router.get('/profile', isAuthenticated, (req, res) => {
  res.json({ message: 'This is your profile data' });
});

module.exports = router;
