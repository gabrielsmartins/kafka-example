package br.com.ecommerce.serialization;

import br.com.ecommerce.common.CorrelationId;
import br.com.ecommerce.common.Message;
import com.google.gson.*;

import java.lang.reflect.Type;

public class MessageAdapter implements JsonSerializer<Message>, JsonDeserializer<Message> {
    @Override
    public JsonElement serialize(Message message, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.add("type", context.serialize(message.getPayload().getClass().getName()));
        object.add("id", context.serialize(message.getId()));
        object.add("payload", context.serialize(message.getPayload()));
        return object;
    }

    @Override
    public Message deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        var payloadType = jsonObject.get("type").getAsString();
        var correlationId = (CorrelationId) context.deserialize(jsonObject.get("id"), CorrelationId.class);
        try {
            var payload = context.deserialize(jsonObject.get("payload"), Class.forName(payloadType));
            return new Message(correlationId, payload);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
