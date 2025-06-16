const mongoose = require('mongoose');
const fs = require('fs');
const path = require('path');

// Підключення до MongoDB
mongoose.connect('mongodb+srv://anastasiabystrytska14:4SsZ2Swzje50FJLs@cluster0.1jbra09.mongodb.net/', {
  dbName: 'test', 
});

mongoose.connection.once('open', async () => {
  console.log('Підключення до MongoDB встановлено');

  const BACKUP_DIR = path.join(__dirname, 'manual-backup');
  if (!fs.existsSync(BACKUP_DIR)) {
    fs.mkdirSync(BACKUP_DIR);
  }

  const collections = await mongoose.connection.db.listCollections().toArray();

  for (const collection of collections) {
    const data = await mongoose.connection.db.collection(collection.name).find().toArray();
    fs.writeFileSync(
      path.join(BACKUP_DIR, `${collection.name}.json`),
      JSON.stringify(data, null, 2)
    );
    console.log(`Збережено колекцію ${collection.name}`);
  }

  mongoose.connection.close();
});
