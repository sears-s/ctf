package org.jivesoftware.smackx.xdata;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.xdata.FormField.Type;
import org.jivesoftware.smackx.xdata.packet.DataForm;

public class Form {
    private DataForm dataForm;

    /* renamed from: org.jivesoftware.smackx.xdata.Form$1 reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smackx$xdata$FormField$Type = new int[Type.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smackx$xdata$FormField$Type[Type.text_multi.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jivesoftware$smackx$xdata$FormField$Type[Type.text_private.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$jivesoftware$smackx$xdata$FormField$Type[Type.text_single.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$jivesoftware$smackx$xdata$FormField$Type[Type.jid_single.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$jivesoftware$smackx$xdata$FormField$Type[Type.hidden.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$jivesoftware$smackx$xdata$FormField$Type[Type.jid_multi.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$org$jivesoftware$smackx$xdata$FormField$Type[Type.list_multi.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$org$jivesoftware$smackx$xdata$FormField$Type[Type.list_single.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
        }
    }

    public static Form getFormFrom(Stanza packet) {
        DataForm dataForm2 = DataForm.from(packet);
        if (dataForm2 == null || dataForm2.getReportedData() != null) {
            return null;
        }
        return new Form(dataForm2);
    }

    public Form(DataForm dataForm2) {
        this.dataForm = dataForm2;
    }

    public Form(DataForm.Type type) {
        this.dataForm = new DataForm(type);
    }

    public void addField(FormField field) {
        this.dataForm.addField(field);
    }

    public void setAnswer(String variable, String value) {
        FormField field = getField(variable);
        if (field != null) {
            int i = AnonymousClass1.$SwitchMap$org$jivesoftware$smackx$xdata$FormField$Type[field.getType().ordinal()];
            if (i == 1 || i == 2 || i == 3 || i == 4 || i == 5) {
                setAnswer(field, (Object) value);
                return;
            }
            throw new IllegalArgumentException("This field is not of type String.");
        }
        throw new IllegalArgumentException("Field not found for the specified variable name.");
    }

    public void setAnswer(String variable, int value) {
        FormField field = getField(variable);
        if (field != null) {
            validateThatFieldIsText(field);
            setAnswer(field, (Object) Integer.valueOf(value));
            return;
        }
        throw new IllegalArgumentException("Field not found for the specified variable name.");
    }

    public void setAnswer(String variable, long value) {
        FormField field = getField(variable);
        if (field != null) {
            validateThatFieldIsText(field);
            setAnswer(field, (Object) Long.valueOf(value));
            return;
        }
        throw new IllegalArgumentException("Field not found for the specified variable name.");
    }

    public void setAnswer(String variable, float value) {
        FormField field = getField(variable);
        if (field != null) {
            validateThatFieldIsText(field);
            setAnswer(field, (Object) Float.valueOf(value));
            return;
        }
        throw new IllegalArgumentException("Field not found for the specified variable name.");
    }

    public void setAnswer(String variable, double value) {
        FormField field = getField(variable);
        if (field != null) {
            validateThatFieldIsText(field);
            setAnswer(field, (Object) Double.valueOf(value));
            return;
        }
        throw new IllegalArgumentException("Field not found for the specified variable name.");
    }

    private static void validateThatFieldIsText(FormField field) {
        int i = AnonymousClass1.$SwitchMap$org$jivesoftware$smackx$xdata$FormField$Type[field.getType().ordinal()];
        if (i != 1 && i != 2 && i != 3) {
            throw new IllegalArgumentException("This field is not of type text (multi, private or single).");
        }
    }

    public void setAnswer(String variable, boolean value) {
        FormField field = getField(variable);
        if (field == null) {
            throw new IllegalArgumentException("Field not found for the specified variable name.");
        } else if (field.getType() == Type.bool) {
            setAnswer(field, (Object) Boolean.toString(value));
        } else {
            throw new IllegalArgumentException("This field is not of type boolean.");
        }
    }

    private void setAnswer(FormField field, Object value) {
        if (isSubmitType()) {
            field.resetValues();
            field.addValue((CharSequence) value.toString());
            return;
        }
        throw new IllegalStateException("Cannot set an answer if the form is not of type \"submit\"");
    }

    public void setAnswer(String variable, List<? extends CharSequence> values) {
        if (isSubmitType()) {
            FormField field = getField(variable);
            if (field != null) {
                int i = AnonymousClass1.$SwitchMap$org$jivesoftware$smackx$xdata$FormField$Type[field.getType().ordinal()];
                if (i == 1 || i == 5 || i == 6 || i == 7 || i == 8) {
                    field.resetValues();
                    field.addValues(values);
                    return;
                }
                throw new IllegalArgumentException("This field only accept list of values.");
            }
            throw new IllegalArgumentException("Couldn't find a field for the specified variable.");
        }
        throw new IllegalStateException("Cannot set an answer if the form is not of type \"submit\"");
    }

    public void setDefaultAnswer(String variable) {
        if (isSubmitType()) {
            FormField field = getField(variable);
            if (field != null) {
                field.resetValues();
                for (CharSequence value : field.getValues()) {
                    field.addValue(value);
                }
                return;
            }
            throw new IllegalArgumentException("Couldn't find a field for the specified variable.");
        }
        throw new IllegalStateException("Cannot set an answer if the form is not of type \"submit\"");
    }

    public List<FormField> getFields() {
        return this.dataForm.getFields();
    }

    public FormField getField(String variable) {
        return this.dataForm.getField(variable);
    }

    public boolean hasField(String variable) {
        return this.dataForm.hasField(variable);
    }

    public String getInstructions() {
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = this.dataForm.getInstructions().iterator();
        while (it.hasNext()) {
            sb.append((String) it.next());
            if (it.hasNext()) {
                sb.append(10);
            }
        }
        return sb.toString();
    }

    public String getTitle() {
        return this.dataForm.getTitle();
    }

    public DataForm.Type getType() {
        return this.dataForm.getType();
    }

    public void setInstructions(String instructions) {
        ArrayList<String> instructionsList = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(instructions, "\n");
        while (st.hasMoreTokens()) {
            instructionsList.add(st.nextToken());
        }
        this.dataForm.setInstructions(instructionsList);
    }

    public void setTitle(String title) {
        this.dataForm.setTitle(title);
    }

    public DataForm getDataFormToSend() {
        if (!isSubmitType()) {
            return this.dataForm;
        }
        DataForm dataFormToSend = new DataForm(getType());
        for (FormField field : getFields()) {
            if (!field.getValues().isEmpty()) {
                dataFormToSend.addField(field);
            }
        }
        return dataFormToSend;
    }

    private boolean isFormType() {
        return DataForm.Type.form == this.dataForm.getType();
    }

    private boolean isSubmitType() {
        return DataForm.Type.submit == this.dataForm.getType();
    }

    public Form createAnswerForm() {
        if (isFormType()) {
            Form form = new Form(DataForm.Type.submit);
            for (FormField field : getFields()) {
                if (field.getVariable() != null) {
                    FormField newField = new FormField(field.getVariable());
                    newField.setType(field.getType());
                    form.addField(newField);
                    if (field.getType() == Type.hidden) {
                        List<CharSequence> values = new ArrayList<>();
                        values.addAll(field.getValues());
                        form.setAnswer(field.getVariable(), values);
                    }
                }
            }
            return form;
        }
        throw new IllegalStateException("Only forms of type \"form\" could be answered");
    }
}
