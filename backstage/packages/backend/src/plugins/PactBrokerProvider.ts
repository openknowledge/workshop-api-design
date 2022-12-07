import {
    ComponentEntityV1alpha1,
    isComponentEntity,
    Entity
} from "@backstage/catalog-model";
import { EntityProvider, EntityProviderConnection } from '@backstage/plugin-catalog-node';
import {PactsEntity} from "./types/PactsEntity";
import { readFileSync } from 'fs';

const catalogFiles = new Map<string, string>([
    ["address-validation-service", "./../../examples/address-validation-service/catalog.json"],
    ["billing-service", "./../../examples/billing-service/catalog.json"],
    ["customer-service", "./../../examples/customer-service/catalog.json"],
    ["delivery-service", "./../../examples/delivery-service/catalog.json"]
]);

function findComponentEntityByName(entities: Entity[], componentName: string): ComponentEntityV1alpha1 | undefined {
    const componentEntities: Entity[] = entities
        .filter(entity => isComponentEntity(entity));
    let entity = componentEntities.find(entity => entity.metadata.name === componentName);
    if (entity === undefined || !isComponentEntity(entity)) {
        return undefined;
    }
    return entity;
}

/**
 * Provides entities from fictional frobs service.
 */
export class PactBrokerProvider implements EntityProvider {
    private readonly env: string;
    private readonly pactApiUrl: string;
    protected connection?: EntityProviderConnection;

    /** [1] */
    constructor(env: string, pactApiUrl: string) {
        this.env = env;
        this.pactApiUrl = pactApiUrl;
    }

    /** [2] */
    getProviderName(): string {
        return `pact-broker-provider-${this.env}`;
    }

    /** [3] */
    async connect(connection: EntityProviderConnection): Promise<void> {
        this.connection = connection;
    }

    /** [4] */
    async run(): Promise<void> {
        if (!this.connection) {
            throw new Error('PactBrokerProvider is not initialized');
        }

        // /** [5] */
        // Add all services from catalog to entities array
        let entities: Entity[] = [];
        catalogFiles.forEach((path) => {
            const catalogEntities: Entity[] = JSON.parse(readFileSync(path, 'utf-8'));
            catalogEntities.forEach(e => entities.push(e));
        })

        // fetch pact response from pact-API and parse to the pactEntity type
        const response = await fetch(this.pactApiUrl);
        const pactEntity: PactsEntity = await response.json();

        // Add the API to the consumer entity for each pact
        pactEntity.pacts.forEach(pact => {
            const apiName = pact._embedded.provider.name + '-api';

            let consumerEntity = findComponentEntityByName(entities, pact._embedded.consumer.name);
            if (consumerEntity !== undefined) {
                consumerEntity.spec.consumesApis?.push(apiName);
            }
        });

        console.log("pact-broker-provider: Successfully executed!")

        /** [6] */
        await this.connection.applyMutation({
            type: 'full',
            entities: entities.map((entity) => ({
                entity,
                locationKey: `pact-broker-provider:${this.env}`,
            }))
        });
    }
}
