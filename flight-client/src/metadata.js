import {
    JsonSerializer
} from 'rsocket-core';

export class Metadata extends Map {

    constructor(json) {
        super();
        if (json != null) {
            for (let [key, value] of json) {
                this.set(key, value);
            }
        }
    }

    toJSON() {
        const result = {};
        for (let [key, value] of this.entries()) {
            result[key] = value;
        }
        return result;
    }

}
Metadata.ROUTE = "route";
Metadata.AUTHENTICATION_BEARER = "message/x.rsocket.authentication.bearer.v0";

export const JsonMetadataSerializer = {

    deserialize(data) {
        if (data == null) {
            return null;
        }
        let json = JsonSerializer.deserialize(data);
        return new Metadata(json);
    },

    serialize(metadata) {
        if (metadata == null) {
            return null;
        }
        let json = metadata.toJSON();
        return JsonSerializer.serialize(json);
    }

};
JsonMetadataSerializer.MIME_TYPE = "application/vnd.spring.rsocket.metadata+json";