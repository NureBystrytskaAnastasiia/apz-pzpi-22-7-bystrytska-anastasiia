const express = require('express');
const router = express.Router();
const sensorDataController = require('../controllers/sensorDataController');

router.post('/', sensorDataController.createSensorData);
router.get('/:roomId', sensorDataController.getSensorDataByRoom);
router.get('/archive/all', sensorDataController.getAllRoomsArchive);

module.exports = router;