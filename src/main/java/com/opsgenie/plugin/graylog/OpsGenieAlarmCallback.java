package com.opsgenie.plugin.graylog;

import com.google.common.collect.Maps;
import org.graylog2.plugin.alarms.AlertCondition;
import org.graylog2.plugin.alarms.callbacks.AlarmCallback;
import org.graylog2.plugin.alarms.callbacks.AlarmCallbackConfigurationException;
import org.graylog2.plugin.alarms.callbacks.AlarmCallbackException;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.configuration.ConfigurationException;
import org.graylog2.plugin.configuration.ConfigurationRequest;
import org.graylog2.plugin.configuration.fields.ConfigurationField;
import org.graylog2.plugin.configuration.fields.TextField;
import org.graylog2.plugin.streams.Stream;

import java.util.Map;


public class OpsGenieAlarmCallback implements AlarmCallback {
    private static final String API_KEY = "api_key";
    private static final String TEAMS = "teams";
    private static final String TAGS = "tags";
    private static final String RECIPIENTS = "recipients";


    private Configuration configuration;

    @Override
    public void initialize(Configuration configuration) throws AlarmCallbackConfigurationException {
        this.configuration = configuration;
    }

    @Override
    public void call(Stream stream, AlertCondition.CheckResult checkResult) throws AlarmCallbackException {
        call(new OpsGenieGraylogClient(configuration.getString(API_KEY), configuration.getString(TAGS),
                configuration.getString(RECIPIENTS), configuration.getString(TEAMS)), stream, checkResult);
    }

    private void call(OpsGenieGraylogClient opsGenieGraylogClient, Stream stream, AlertCondition.CheckResult checkResult) throws AlarmCallbackException {
        opsGenieGraylogClient.trigger(stream, checkResult);
    }


    @Override
    public ConfigurationRequest getRequestedConfiguration() {
        final ConfigurationRequest configurationRequest = new ConfigurationRequest();

        configurationRequest.addField(new TextField(API_KEY,
                "OpsGenie API key", "",
                "OpsGenie API integration key",
                ConfigurationField.Optional.NOT_OPTIONAL));

        configurationRequest.addField(new TextField(TAGS,
                "Tags", "",
                "Comma separated list of alert tags",
                ConfigurationField.Optional.OPTIONAL));

        configurationRequest.addField(new TextField(TEAMS,
                "Teams", "",
                "Comma separated list of teams",
                ConfigurationField.Optional.OPTIONAL));

        configurationRequest.addField(new TextField(RECIPIENTS,
                "Recipients", "",
                "Comma separated list of recipients",
                ConfigurationField.Optional.OPTIONAL));


        return configurationRequest;
    }

    @Override
    public String getName() {
        return "OpsGenie alarm callback";
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Maps.transformEntries(configuration.getSource(), new Maps.EntryTransformer<String, Object, Object>() {
            @Override
            public Object transformEntry(String key, Object value) {
                if (API_KEY.equals(key)) {
                    return "****";
                }
                return value;
            }
        });
    }

    @Override
    public void checkConfiguration() throws ConfigurationException {
        if (!configuration.stringIsSet(API_KEY)) {
            throw new ConfigurationException(API_KEY + " is mandatory and must be not be null or empty.");
        }
    }
}
