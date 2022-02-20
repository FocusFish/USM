package fish.focus.uvms.usm.administration.service;

import fish.focus.uvms.commons.date.JsonBConfigurator;

import javax.json.bind.config.BinaryDataStrategy;

public class JsonBConfiguratorExtended extends JsonBConfigurator {

    public JsonBConfiguratorExtended() {
        super();
        withConfig();
    }

    public void withConfig() {
        config.withBinaryDataStrategy(BinaryDataStrategy.BASE_64);
    }
}
