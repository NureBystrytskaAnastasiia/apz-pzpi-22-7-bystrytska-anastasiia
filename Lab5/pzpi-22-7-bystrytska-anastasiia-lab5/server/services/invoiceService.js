const pdf = require('html-pdf');
const moment = require('moment');

const generateInvoice = async (order) => {
  const invoiceNumber = `INV-${moment().format('YYYYMMDD')}-${Math.floor(1000 + Math.random() * 9000)}`;
  
  const companyInfo = {
    name: 'Аптека "Здоров\'я"',
    address: 'м. Київ, вул. Лікарська, 15',
    phone: '+380 (44) 123-45-67',
    email: 'apteka@zdorovya.com.ua',
    taxId: '1234567890'
  };

  // Отримуємо унікальних постачальників
  const suppliers = [...new Set(order.items.map(item => item.supplier))];
  
  const invoiceTemplate = `
    <html>
      <head>
        <style>
          body { font-family: 'Arial', sans-serif; margin: 0; padding: 20px; color: #333; }
          .header { display: flex; justify-content: space-between; margin-bottom: 30px; }
          .invoice-title { text-align: center; margin-bottom: 30px; }
          .invoice-title h1 { color: #2c3e50; margin-bottom: 5px; }
          .invoice-info { margin-bottom: 30px; }
          .info-box { margin-bottom: 15px; }
          .info-box h3 { border-bottom: 1px solid #ddd; padding-bottom: 5px; }
          table { width: 100%; border-collapse: collapse; margin: 20px 0; }
          th { background-color: #3498db; color: white; text-align: left; padding: 10px; }
          td { padding: 10px; border-bottom: 1px solid #ddd; }
          .total-row { font-weight: bold; }
          .footer { margin-top: 50px; font-size: 12px; text-align: center; color: #7f8c8d; }
          .signature { margin-top: 50px; display: flex; justify-content: space-between; }
          .signature div { width: 40%; border-top: 1px solid #333; padding-top: 10px; }
        </style>
      </head>
      <body>
        <div class="header">
          <div>
            <h2>${companyInfo.name}</h2>
            <p>${companyInfo.address}</p>
            <p>Тел: ${companyInfo.phone}</p>
            <p>ЄДРПОУ: ${companyInfo.taxId}</p>
          </div>
        </div>

        <div class="invoice-title">
          <h1>Накладна № ${invoiceNumber}</h1>
          <p>Дата: ${moment().format('DD.MM.YYYY')}</p>
        </div>

        <div class="invoice-info">
          <div class="info-box">
            <h3>Постачальник${suppliers.length > 1 ? 'и' : ''}</h3>
            ${suppliers.map(supplier => `<p>${supplier}</p>`).join('')}
          </div>
          <div class="info-box">
            <h3>Отримувач</h3>
            <p>${companyInfo.name}</p>
            <p>${companyInfo.address}</p>
            <p>Тел: ${companyInfo.phone}</p>
          </div>
        </div>

        <table>
          <thead>
            <tr>
              <th>№</th>
              <th>Найменування</th>
              <th>Постачальник</th>
              <th>Одиниця</th>
              <th>Кількість</th>
              <th>Ціна за од.</th>
              <th>Сума</th>
            </tr>
          </thead>
          <tbody>
            ${order.items.map((item, index) => `
              <tr>
                <td>${index + 1}</td>
                <td>${item.medicineName}</td>
                <td>${item.supplier}</td>
                <td>уп.</td>
                <td>${item.quantity}</td>
                <td>${item.unitPrice.toFixed(2)} грн</td>
                <td>${item.totalPrice.toFixed(2)} грн</td>
              </tr>
            `).join('')}
            <tr class="total-row">
              <td colspan="6" style="text-align: right;">Разом:</td>
              <td>${order.totalAmount.toFixed(2)} грн</td>
            </tr>
          </tbody>
        </table>

        <div class="signature">
          <div>
            <p>Відповідальна особа постачальника</p>
            <p>_________________________</p>
          </div>
          <div>
            <p>Відповідальна особа отримувача</p>
            <p>_________________________</p>
          </div>
        </div>

        <div class="footer">
          <p>Ця накладна згенерована автоматично. Дякуємо за співпрацю!</p>
          <p>${companyInfo.name} | ${companyInfo.address} | Тел: ${companyInfo.phone} | Email: ${companyInfo.email}</p>
        </div>
      </body>
    </html>
  `;

  const pdfOptions = {
    format: 'A4',
    border: {
      top: '10mm',
      right: '10mm',
      bottom: '10mm',
      left: '10mm'
    },
    footer: {
      height: '10mm',
      contents: {
        default: `<div style="text-align: center; font-size: 10px; color: #666;">
          Сторінка {{page}} з {{pages}}
        </div>`
      }
    }
  };

  const pdfBuffer = await new Promise((resolve, reject) => {
    pdf.create(invoiceTemplate, pdfOptions).toBuffer((err, buffer) => {
      if (err) reject(err);
      else resolve(buffer);
    });
  });

  return {
    html: invoiceTemplate,
    pdf: pdfBuffer.toString('base64'),
    invoiceNumber: invoiceNumber
  };
};

module.exports = { generateInvoice };
