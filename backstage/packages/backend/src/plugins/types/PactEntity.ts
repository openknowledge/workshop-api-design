export interface PactEntity {
    createdAt: string;
    _embedded: {
        consumer: {
            name: string;
            _embedded: {
                version: {
                    number: string;
                }
            }
        }
        provider: {
            name: string;
        }
    };
}