interface Database {
    query(sql: string): any;
}

class RealDatabase implements Database {
    query(sql: string): any {
        console.log(`Executing query: ${sql}`);
        // Реальна логіка запиту до БД
        return { data: "result from DB" };
    }
}
class DatabaseProxy implements Database {
    private realDatabase: RealDatabase;
    private cache: Map<string, any> = new Map();
    
    query(sql: string): any {
        if (this.cache.has(sql)) {
            console.log(`Returning cached result for: ${sql}`);
            return this.cache.get(sql); 
        }
        
        if (!this.realDatabase) {
            this.realDatabase = new RealDatabase();
        }
        
        const result = this.realDatabase.query(sql);
        this.cache.set(sql, result);
        return result;
    }
}
