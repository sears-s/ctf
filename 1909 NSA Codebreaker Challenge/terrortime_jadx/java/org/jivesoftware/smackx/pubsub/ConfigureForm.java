package org.jivesoftware.smackx.pubsub;

import java.util.ArrayList;
import java.util.List;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jivesoftware.smackx.xdata.packet.DataForm.Type;

public class ConfigureForm extends Form {
    public ConfigureForm(DataForm configDataForm) {
        super(configDataForm);
    }

    public ConfigureForm(Form nodeConfigForm) {
        super(nodeConfigForm.getDataFormToSend());
    }

    public ConfigureForm(Type formType) {
        super(formType);
    }

    public AccessModel getAccessModel() {
        String value = getFieldValue(ConfigureNodeFields.access_model);
        if (value == null) {
            return null;
        }
        return AccessModel.valueOf(value);
    }

    public void setAccessModel(AccessModel accessModel) {
        addField(ConfigureNodeFields.access_model, FormField.Type.list_single);
        setAnswer(ConfigureNodeFields.access_model.getFieldName(), getListSingle(accessModel.toString()));
    }

    public String getBodyXSLT() {
        return getFieldValue(ConfigureNodeFields.body_xslt);
    }

    public void setBodyXSLT(String bodyXslt) {
        addField(ConfigureNodeFields.body_xslt, FormField.Type.text_single);
        setAnswer(ConfigureNodeFields.body_xslt.getFieldName(), bodyXslt);
    }

    public List<String> getChildren() {
        return getFieldValues(ConfigureNodeFields.children);
    }

    public void setChildren(List<String> children) {
        addField(ConfigureNodeFields.children, FormField.Type.text_multi);
        setAnswer(ConfigureNodeFields.children.getFieldName(), children);
    }

    public ChildrenAssociationPolicy getChildrenAssociationPolicy() {
        String value = getFieldValue(ConfigureNodeFields.children_association_policy);
        if (value == null) {
            return null;
        }
        return ChildrenAssociationPolicy.valueOf(value);
    }

    public void setChildrenAssociationPolicy(ChildrenAssociationPolicy policy) {
        addField(ConfigureNodeFields.children_association_policy, FormField.Type.list_single);
        List<String> values = new ArrayList<>(1);
        values.add(policy.toString());
        setAnswer(ConfigureNodeFields.children_association_policy.getFieldName(), values);
    }

    public List<String> getChildrenAssociationWhitelist() {
        return getFieldValues(ConfigureNodeFields.children_association_whitelist);
    }

    public void setChildrenAssociationWhitelist(List<String> whitelist) {
        addField(ConfigureNodeFields.children_association_whitelist, FormField.Type.jid_multi);
        setAnswer(ConfigureNodeFields.children_association_whitelist.getFieldName(), whitelist);
    }

    public int getChildrenMax() {
        return Integer.parseInt(getFieldValue(ConfigureNodeFields.children_max));
    }

    public void setChildrenMax(int max) {
        addField(ConfigureNodeFields.children_max, FormField.Type.text_single);
        setAnswer(ConfigureNodeFields.children_max.getFieldName(), max);
    }

    public String getCollection() {
        return getFieldValue(ConfigureNodeFields.collection);
    }

    public void setCollection(String collection) {
        addField(ConfigureNodeFields.collection, FormField.Type.text_single);
        setAnswer(ConfigureNodeFields.collection.getFieldName(), collection);
    }

    public String getDataformXSLT() {
        return getFieldValue(ConfigureNodeFields.dataform_xslt);
    }

    public void setDataformXSLT(String url) {
        addField(ConfigureNodeFields.dataform_xslt, FormField.Type.text_single);
        setAnswer(ConfigureNodeFields.dataform_xslt.getFieldName(), url);
    }

    public boolean isDeliverPayloads() {
        return parseBoolean(getFieldValue(ConfigureNodeFields.deliver_payloads));
    }

    public void setDeliverPayloads(boolean deliver) {
        addField(ConfigureNodeFields.deliver_payloads, FormField.Type.bool);
        setAnswer(ConfigureNodeFields.deliver_payloads.getFieldName(), deliver);
    }

    public ItemReply getItemReply() {
        String value = getFieldValue(ConfigureNodeFields.itemreply);
        if (value == null) {
            return null;
        }
        return ItemReply.valueOf(value);
    }

    public void setItemReply(ItemReply reply) {
        addField(ConfigureNodeFields.itemreply, FormField.Type.list_single);
        setAnswer(ConfigureNodeFields.itemreply.getFieldName(), getListSingle(reply.toString()));
    }

    public int getMaxItems() {
        return Integer.parseInt(getFieldValue(ConfigureNodeFields.max_items));
    }

    public void setMaxItems(int max) {
        addField(ConfigureNodeFields.max_items, FormField.Type.text_single);
        setAnswer(ConfigureNodeFields.max_items.getFieldName(), max);
    }

    public int getMaxPayloadSize() {
        return Integer.parseInt(getFieldValue(ConfigureNodeFields.max_payload_size));
    }

    public void setMaxPayloadSize(int max) {
        addField(ConfigureNodeFields.max_payload_size, FormField.Type.text_single);
        setAnswer(ConfigureNodeFields.max_payload_size.getFieldName(), max);
    }

    public NodeType getNodeType() {
        String value = getFieldValue(ConfigureNodeFields.node_type);
        if (value == null) {
            return null;
        }
        return NodeType.valueOf(value);
    }

    public void setNodeType(NodeType type) {
        addField(ConfigureNodeFields.node_type, FormField.Type.list_single);
        setAnswer(ConfigureNodeFields.node_type.getFieldName(), getListSingle(type.toString()));
    }

    public boolean isNotifyConfig() {
        return parseBoolean(getFieldValue(ConfigureNodeFields.notify_config));
    }

    public void setNotifyConfig(boolean notify) {
        addField(ConfigureNodeFields.notify_config, FormField.Type.bool);
        setAnswer(ConfigureNodeFields.notify_config.getFieldName(), notify);
    }

    public boolean isNotifyDelete() {
        return parseBoolean(getFieldValue(ConfigureNodeFields.notify_delete));
    }

    public void setNotifyDelete(boolean notify) {
        addField(ConfigureNodeFields.notify_delete, FormField.Type.bool);
        setAnswer(ConfigureNodeFields.notify_delete.getFieldName(), notify);
    }

    public boolean isNotifyRetract() {
        return parseBoolean(getFieldValue(ConfigureNodeFields.notify_retract));
    }

    public void setNotifyRetract(boolean notify) {
        addField(ConfigureNodeFields.notify_retract, FormField.Type.bool);
        setAnswer(ConfigureNodeFields.notify_retract.getFieldName(), notify);
    }

    public NotificationType getNotificationType() {
        String value = getFieldValue(ConfigureNodeFields.notification_type);
        if (value == null) {
            return null;
        }
        return NotificationType.valueOf(value);
    }

    public void setNotificationType(NotificationType notificationType) {
        addField(ConfigureNodeFields.notification_type, FormField.Type.list_single);
        setAnswer(ConfigureNodeFields.notification_type.getFieldName(), getListSingle(notificationType.toString()));
    }

    public boolean isPersistItems() {
        return parseBoolean(getFieldValue(ConfigureNodeFields.persist_items));
    }

    public void setPersistentItems(boolean persist) {
        addField(ConfigureNodeFields.persist_items, FormField.Type.bool);
        setAnswer(ConfigureNodeFields.persist_items.getFieldName(), persist);
    }

    public boolean isPresenceBasedDelivery() {
        return parseBoolean(getFieldValue(ConfigureNodeFields.presence_based_delivery));
    }

    public void setPresenceBasedDelivery(boolean presenceBased) {
        addField(ConfigureNodeFields.presence_based_delivery, FormField.Type.bool);
        setAnswer(ConfigureNodeFields.presence_based_delivery.getFieldName(), presenceBased);
    }

    public PublishModel getPublishModel() {
        String value = getFieldValue(ConfigureNodeFields.publish_model);
        if (value == null) {
            return null;
        }
        return PublishModel.valueOf(value);
    }

    public void setPublishModel(PublishModel publish) {
        addField(ConfigureNodeFields.publish_model, FormField.Type.list_single);
        setAnswer(ConfigureNodeFields.publish_model.getFieldName(), getListSingle(publish.toString()));
    }

    public List<String> getReplyRoom() {
        return getFieldValues(ConfigureNodeFields.replyroom);
    }

    public void setReplyRoom(List<String> replyRooms) {
        addField(ConfigureNodeFields.replyroom, FormField.Type.list_multi);
        setAnswer(ConfigureNodeFields.replyroom.getFieldName(), replyRooms);
    }

    public List<String> getReplyTo() {
        return getFieldValues(ConfigureNodeFields.replyto);
    }

    public void setReplyTo(List<String> replyTos) {
        addField(ConfigureNodeFields.replyto, FormField.Type.list_multi);
        setAnswer(ConfigureNodeFields.replyto.getFieldName(), replyTos);
    }

    public List<String> getRosterGroupsAllowed() {
        return getFieldValues(ConfigureNodeFields.roster_groups_allowed);
    }

    public void setRosterGroupsAllowed(List<String> groups) {
        addField(ConfigureNodeFields.roster_groups_allowed, FormField.Type.list_multi);
        setAnswer(ConfigureNodeFields.roster_groups_allowed.getFieldName(), groups);
    }

    @Deprecated
    public boolean isSubscibe() {
        return isSubscribe();
    }

    public boolean isSubscribe() {
        return parseBoolean(getFieldValue(ConfigureNodeFields.subscribe));
    }

    public void setSubscribe(boolean subscribe) {
        addField(ConfigureNodeFields.subscribe, FormField.Type.bool);
        setAnswer(ConfigureNodeFields.subscribe.getFieldName(), subscribe);
    }

    public String getTitle() {
        return getFieldValue(ConfigureNodeFields.title);
    }

    public void setTitle(String title) {
        addField(ConfigureNodeFields.title, FormField.Type.text_single);
        setAnswer(ConfigureNodeFields.title.getFieldName(), title);
    }

    public String getDataType() {
        return getFieldValue(ConfigureNodeFields.type);
    }

    public void setDataType(String type) {
        addField(ConfigureNodeFields.type, FormField.Type.text_single);
        setAnswer(ConfigureNodeFields.type.getFieldName(), type);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName());
        sb.append(" Content [");
        StringBuilder result = new StringBuilder(sb.toString());
        for (FormField formField : getFields()) {
            result.append('(');
            result.append(formField.getVariable());
            result.append(':');
            StringBuilder valuesBuilder = new StringBuilder();
            for (CharSequence value : formField.getValues()) {
                if (valuesBuilder.length() > 0) {
                    result.append(',');
                }
                valuesBuilder.append(value);
            }
            if (valuesBuilder.length() == 0) {
                valuesBuilder.append("NOT SET");
            }
            result.append(valuesBuilder);
            result.append(')');
        }
        result.append(']');
        return result.toString();
    }

    private static boolean parseBoolean(String fieldValue) {
        return "1".equals(fieldValue) || "true".equals(fieldValue);
    }

    private String getFieldValue(ConfigureNodeFields field) {
        return getField(field.getFieldName()).getFirstValue();
    }

    private List<String> getFieldValues(ConfigureNodeFields field) {
        return getField(field.getFieldName()).getValuesAsString();
    }

    private void addField(ConfigureNodeFields nodeField, FormField.Type type) {
        String fieldName = nodeField.getFieldName();
        if (getField(fieldName) == null) {
            FormField field = new FormField(fieldName);
            field.setType(type);
            addField(field);
        }
    }

    private static List<String> getListSingle(String value) {
        List<String> list = new ArrayList<>(1);
        list.add(value);
        return list;
    }
}
