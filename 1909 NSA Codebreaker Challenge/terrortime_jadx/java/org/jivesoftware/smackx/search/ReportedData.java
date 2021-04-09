package org.jivesoftware.smackx.search;

import com.badguy.terrortime.BuildConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.FormField.Type;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jivesoftware.smackx.xdata.packet.DataForm.Item;

public class ReportedData {
    private final List<Column> columns = new ArrayList();
    private final List<Row> rows = new ArrayList();
    private String title = BuildConfig.FLAVOR;

    public static class Column {
        private final String label;
        private final Type type;
        private final String variable;

        public Column(String label2, String variable2, Type type2) {
            this.label = label2;
            this.variable = variable2;
            this.type = type2;
        }

        public String getLabel() {
            return this.label;
        }

        public Type getType() {
            return this.type;
        }

        public String getVariable() {
            return this.variable;
        }
    }

    public static class Field {
        private final List<? extends CharSequence> values;
        private final String variable;

        public Field(String variable2, List<? extends CharSequence> values2) {
            this.variable = variable2;
            this.values = values2;
        }

        public String getVariable() {
            return this.variable;
        }

        public List<CharSequence> getValues() {
            return Collections.unmodifiableList(this.values);
        }
    }

    public static class Row {
        private List<Field> fields = new ArrayList();

        public Row(List<Field> fields2) {
            this.fields = fields2;
        }

        public List<CharSequence> getValues(String variable) {
            for (Field field : getFields()) {
                if (variable.equalsIgnoreCase(field.getVariable())) {
                    return field.getValues();
                }
            }
            return null;
        }

        private List<Field> getFields() {
            return Collections.unmodifiableList(new ArrayList(this.fields));
        }
    }

    public static ReportedData getReportedDataFrom(Stanza packet) {
        DataForm dataForm = DataForm.from(packet);
        if (dataForm == null || dataForm.getReportedData() == null) {
            return null;
        }
        return new ReportedData(dataForm);
    }

    private ReportedData(DataForm dataForm) {
        for (FormField field : dataForm.getReportedData().getFields()) {
            this.columns.add(new Column(field.getLabel(), field.getVariable(), field.getType()));
        }
        for (Item item : dataForm.getItems()) {
            List<Field> fieldList = new ArrayList<>(this.columns.size());
            for (FormField field2 : item.getFields()) {
                List<CharSequence> values = new ArrayList<>();
                values.addAll(field2.getValues());
                fieldList.add(new Field(field2.getVariable(), values));
            }
            this.rows.add(new Row(fieldList));
        }
        this.title = dataForm.getTitle();
    }

    public ReportedData() {
    }

    public void addRow(Row row) {
        this.rows.add(row);
    }

    public void addColumn(Column column) {
        this.columns.add(column);
    }

    public List<Row> getRows() {
        return Collections.unmodifiableList(new ArrayList(this.rows));
    }

    public List<Column> getColumns() {
        return Collections.unmodifiableList(new ArrayList(this.columns));
    }

    public String getTitle() {
        return this.title;
    }
}
