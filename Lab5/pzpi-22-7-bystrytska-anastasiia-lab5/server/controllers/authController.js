const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const User = require('../models/User');
const sendEmail = require('../utils/sendEmail');
const nodemailer = require('nodemailer');



const register = async (req, res) => {
  const { email, password, name, role } = req.body;
  try {
    const existing = await User.findOne({ email });
    if (existing) return res.status(400).json({ message: 'Email already in use' });

    const hashedPassword = await bcrypt.hash(password, 10);
    const user = new User({ email, password: hashedPassword, name, role });
    await user.save();

    res.status(201).json({ message: 'User registered successfully' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

const login = async (req, res) => {
  const { email, password } = req.body;
  try {
    const user = await User.findOne({ email });
    if (!user) return res.status(400).json({ message: 'Invalid credentials' });

    const valid = await bcrypt.compare(password, user.password);
    if (!valid) return res.status(400).json({ message: 'Invalid credentials' });

    const token = jwt.sign({ id: user._id, role: user.role }, process.env.JWT_SECRET, { expiresIn: '1d' });

    // Збереження користувача в сесії
    req.session.userId = user._id;
    req.session.role = user.role;
    req.session.email = user.email;

    res.json({ token, user: { email: user.email, name: user.name, role: user.role } });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};


const forgotPassword = async (req, res) => {
  const { email } = req.body; // отримуємо email з тіла запиту
  
  try {
    // Перевірка, чи існує користувач з таким email
    const user = await User.findOne({ email });
    if (!user) {
      return res.status(404).json({ message: 'Користувача з такою поштою не знайдено' });
    }

    // Генерація нового пароля
    const newPassword = Math.floor(10000000 + Math.random() * 90000000).toString();
    
    // Хешування нового пароля
    const hashedPassword = await bcrypt.hash(newPassword, 10);
    user.password = hashedPassword; // Оновлюємо пароль в базі даних
    await user.save(); // Зберігаємо зміни в базі даних

    // Налаштування транспортеру для відправки листа через Gmail
    const transporter = nodemailer.createTransport({
      service: 'gmail',
      auth: {
        user: 'anastasiia.bystrytska@nure.ua',
        pass: 'lszr tvgs bgwv qhhm', 
      }
    });

    const mailOptions = {
      from: '"Support Team" <anastasiia.bystrytska@nure.ua>',
      to: email,
      subject: 'Відновлення паролю до вашого акаунту',
      html: `
        <div style="font-family: 'Segoe UI', 'Roboto', 'Helvetica Neue', sans-serif; max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); overflow: hidden;">
          <div style="background-color: #2c3e50; color: #ffffff; padding: 20px 30px;">
            <h2 style="margin: 0; font-weight: 600;">🔐 Відновлення паролю</h2>
          </div>
          <div style="padding: 30px;">
            <p style="font-size: 16px;">Шановний користувачу,</p>
            <p style="font-size: 16px;">Ми отримали запит на відновлення паролю для вашого акаунту.</p>
    
            <div style="background-color: #ecf0f1; padding: 20px; margin: 25px 0; border-radius: 8px; text-align: center;">
              <p style="margin: 0; font-size: 18px;">Ваш тимчасовий пароль:</p>
              <p style="margin: 10px 0 0 0; font-size: 22px; font-weight: bold; color: #e74c3c;">${newPassword}</p>
            </div>
    
            <p style="font-size: 16px;">Будь ласка, використовуйте цей пароль для входу в систему. Після входу ми рекомендуємо <strong>негайно змінити його</strong> у налаштуваннях профілю.</p>
    
            <p style="font-size: 14px; color: #7f8c8d;">Якщо ви не запитували зміну паролю, проігноруйте це повідомлення або зв'яжіться з нашою службою підтримки.</p>
    
            <p style="margin-top: 30px; font-size: 16px;">З повагою,<br><strong>Команда підтримки Apteka</strong></p>
          </div>
        </div>
      `
    };
    
    // Відправка листа
    transporter.sendMail(mailOptions, (err, info) => {
      if (err) {
        console.error('Помилка відправки листа:', err);
        return res.status(500).json({ message: 'Помилка при відправці листа' });
      }
      res.status(200).json({ message: 'Новий пароль надіслано на пошту' });
    });
    
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Помилка сервера' });
  }
};
const logout = (req, res) => {
  req.session.destroy(err => {
    if (err) {
      return res.status(500).json({ message: 'Failed to log out' });
    }
    res.clearCookie('connect.sid'); 
    res.json({ message: 'Logged out successfully' });
  });
};


module.exports = { register, login, forgotPassword, logout };
