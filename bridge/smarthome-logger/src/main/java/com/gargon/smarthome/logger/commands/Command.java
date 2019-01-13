package com.gargon.smarthome.logger.commands;

import com.gargon.smarthome.supradin.messages.SupradinDataMessage;
import com.gargon.smarthome.logger.RealTimeSupradinDataMessage;
import com.gargon.smarthome.logger.commands.filters.Filter;
import com.gargon.smarthome.logger.commands.filters.FilterFactory;
import com.gargon.smarthome.logger.commands.readers.Reader;
import com.gargon.smarthome.logger.commands.readers.ReaderFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author gargon
 */
public abstract class Command {

    private final List<Filter> filters = new ArrayList();
    private final Reader reader;

    public Command(JSONObject config) {
        JSONArray filtersJSON = config.optJSONArray("filters");
        if (filtersJSON != null) {
            for (int i = 0; i < filtersJSON.length(); i++) {
                Filter f = FilterFactory.createFilter(filtersJSON.optJSONObject(i));
                if (f != null) {
                    filters.add(f);
                }

            }
        }
        reader = ReaderFactory.createReader(config.optJSONObject("reader"));
    }

    protected boolean filter(SupradinDataMessage supradin) {
        for (Filter f : filters) {
            if (!f.filter(supradin)) {
                return false;
            }
        }
        return true;
    }
    
    protected Map<String, Object> read(SupradinDataMessage supradin){
        return reader.perform(supradin);
    }
    
    public abstract boolean approve(RealTimeSupradinDataMessage message);

}
