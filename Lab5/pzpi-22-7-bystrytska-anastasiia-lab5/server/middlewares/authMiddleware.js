const jwt = require('jsonwebtoken'); // не забудь підключити jwt

const isAuthenticated = (req, res, next) => {
    if (req.session && req.session.userId) {
        return next();  // Користувач авторизований, продовжуємо
    } else {
        return res.status(403).json({ message: 'User not authenticated' });
    }
};

const isAdmin = (req, res, next) => {
    try {
        const token = req.headers.authorization?.split(' ')[1];
        if (!token) {
            return res.status(401).json({ message: 'Необхідна авторизація' });
        }

        const decoded = jwt.verify(token, process.env.JWT_SECRET);
        if (decoded.role !== 'admin') {
            return res.status(403).json({ message: 'Доступ заборонено' });
        }

        req.user = decoded;
        next();
    } catch (error) {
        return res.status(401).json({ message: 'Неавторизований доступ' });
    }
};

module.exports = {
    isAuthenticated,
    isAdmin
};
