// server.js
const express = require('express');
const mongoose = require('mongoose');
const session = require('express-session');
const MongoStore = require('connect-mongo');
const cors = require('cors');
const dotenv = require('dotenv');

// Імпорт маршрутів
const medicineRoutes = require('./routes/medicine.routes');
const orderRoutes = require('./routes/order.routes');
const roomRoutes = require('./routes/roomRoutes');
const sensorDataRoutes = require('./routes/sensorData.routes');
const authRoutes = require('./routes/auth');
const { simulateEnvironmentChanges } = require('./controllers/roomController');
const userRoutes = require('./routes/user.routes');

// Імпорт MQTT обробки
require('./mqtt');

// Завантаження змінних середовища з .env файлу
dotenv.config();

const app = express();

// ✅ Налаштування CORS
app.use(cors({
  origin: 'http://127.0.0.1:5500', // Дозволити запити з цього домену
  credentials: true
}));

// Налаштування JSON парсера
app.use(express.json());

// Налаштування сесій
app.use(session({
  secret: process.env.SESSION_SECRET || 'your-secret-key', // Секрет для шифрування сесій
  resave: false,
  saveUninitialized: false,
  cookie: { secure: false }, // ❗ HTTPS -> true, локально -> false
  store: MongoStore.create({
    mongoUrl: process.env.MONGO_URI, // URL для MongoDB
    collectionName: 'sessions', // Назва колекції для зберігання сесій
  })
}));

// Налаштування маршрутів
app.use('/api', authRoutes);
app.use('/api', medicineRoutes);
app.use('/api/orders', orderRoutes);
app.use('/api/rooms', roomRoutes);
app.use('/api/sensor', sensorDataRoutes);
app.use('/api', userRoutes);


const SIMULATION_INTERVAL = 30000;
setInterval(() => {
  simulateEnvironmentChanges();
}, SIMULATION_INTERVAL);

simulateEnvironmentChanges();
// Підключення до MongoDB
mongoose.connect(process.env.MONGO_URI, {
  useNewUrlParser: true,
  useUnifiedTopology: true
}).then(() => {
  console.log('MongoDB connected');
  // Запуск сервера
  app.listen(process.env.PORT, () => {
    console.log(`Server running on port ${process.env.PORT}`);
  });
}).catch(err => console.error('MongoDB connection error:', err));
