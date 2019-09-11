import {
    createBuffer,
    byteLength
} from 'rsocket-core';

/**
 * Serializer for Routing metadata
 * @see https://github.com/rsocket/rsocket/blob/master/Extensions/Routing.md
 */
export class RoutingMetadataSerializer {

    deserialize(data) {
        if (data == null) {
            return null;
        }
        return data.toString('utf8', 1, data.length);
    }

    serialize(data) {
        if (data == null) {
            return null;
        }
        let dataLength = byteLength(data, 'utf8');
        let outBuffer = createBuffer(1 + dataLength);
        outBuffer.writeUInt8(dataLength, 0);
        outBuffer.write(data, 1, data.length, 'utf8');
        return outBuffer;
    }
}

RoutingMetadataSerializer.MIME_TYPE = "message/x.rsocket.routing.v0";
RoutingMetadataSerializer.MIME_TYPE_ID = 0x7E;