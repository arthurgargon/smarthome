package com.gargon.smarthome.supradin.utils.config.commands;

import com.gargon.smarthome.supradin.messages.SupradinDataMessage;
import com.gargon.smarthome.supradin.utils.config.commands.filters.Filter;
import com.gargon.smarthome.supradin.utils.config.commands.filters.FilterFactory;
import com.gargon.smarthome.supradin.utils.config.commands.readers.Reader;
import com.gargon.smarthome.supradin.utils.config.commands.readers.ReaderFactory;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author gargon
 */
public abstract class Command {

    private List<Filter> filters = new ArrayList();
    private Reader reader = null;

    public Command(JSONObject config) {
        JSONArray filtersJSON = config.getJSONArray("filters");
        if (filtersJSON != null) {
            for (int i = 0; i < filtersJSON.length(); i++) {
                try {
                    JSONObject filterJSONObject = filtersJSON.getJSONObject(i);
                    Filter f = FilterFactory.createFilter(filterJSONObject);
                    filters.add(f);
                } catch (Exception e) {
                }
            }
        }
        JSONObject readerJSONObject = config.getJSONObject("reader");
        if (readerJSONObject != null) {
            try {
                reader = ReaderFactory.createFilter(readerJSONObject);
            } catch (Exception e) {
                reader = null;
            }
        }
    }

    public Reader getReader() {
        return reader;
    }

    public boolean filter(SupradinDataMessage supradin) {
        for (Filter f : filters) {
            if (!f.filter(supradin)) {
                return false;
            }
        }
        return true;
    }

}
