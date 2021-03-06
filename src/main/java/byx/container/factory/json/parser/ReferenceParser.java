package byx.container.factory.json.parser;

import byx.container.component.Component;
import byx.container.factory.json.JsonElement;

import static byx.container.factory.json.ReservedKey.*;
import static byx.container.component.Component.*;

public class ReferenceParser implements Parser
{
    @Override
    public Component parse(JsonElement element, ParserContext context)
    {
        String id = element.getElement(RESERVED_REF).getString();
        Component c = context.resolveComponentRef(id);
        if (c != null) return c;
        return reference(context.getContainer(), id);
    }
}
