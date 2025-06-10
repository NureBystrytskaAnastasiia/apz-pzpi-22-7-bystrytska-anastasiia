const mongoose = require('mongoose');

const orderItemSchema = new mongoose.Schema({
  medicineId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Medicine',
    required: true
  },
  medicineName: {
    type: String,
    required: true
  },
  supplier: {
    type: String,
    required: true
  },
  quantity: {
    type: Number,
    required: true,
    min: 1
  },
  unitPrice: {
    type: Number,
    required: true,
    min: 0
  },
  totalPrice: {
    type: Number,
    required: true,
    min: 0
  }
});

const orderSchema = new mongoose.Schema({
  items: [orderItemSchema],
  status: {
    type: String,
    enum: ['created', 'delivered', 'completed'],
    default: 'created'
  },
  createdAt: {
    type: Date,
    default: Date.now
  },
  deliveredAt: Date,
  completedAt: Date,
  invoiceNumber: String,
  invoiceData: Object,
  totalAmount: {
    type: Number,
    default: 0
  }
}, { timestamps: true });

module.exports = mongoose.model('Order', orderSchema);