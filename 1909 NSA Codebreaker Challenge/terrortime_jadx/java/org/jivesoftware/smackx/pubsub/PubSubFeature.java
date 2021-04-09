package org.jivesoftware.smackx.pubsub;

import org.jivesoftware.smackx.disco.Feature.Support;

public enum PubSubFeature implements CharSequence {
    access_authorize(Support.optional),
    access_open(Support.optional),
    access_presence(Support.optional),
    access_roster(Support.optional),
    access_whitelist(Support.optional),
    auto_create(Support.optional),
    auto_subscribe(Support.recommended),
    collections(Support.optional),
    config_node(Support.recommended),
    create_and_configure(Support.recommended),
    create_nodes(Support.recommended),
    delete_items(Support.recommended),
    delete_nodes(Support.recommended),
    get_pending(Support.optional),
    item_ids(Support.recommended),
    last_published(Support.recommended),
    leased_subscription(Support.optional),
    manage_subscriptions(Support.optional),
    member_affiliation(Support.recommended),
    meta_data(Support.recommended),
    modify_affiliations(Support.optional),
    multi_collection(Support.optional),
    multi_subscribe(Support.optional),
    outcast_affiliation(Support.recommended),
    persistent_items(Support.recommended),
    presence_notifications(Support.optional),
    presence_subscribe(Support.recommended),
    publish(Support.required),
    publish_options(Support.optional),
    publish_only_affiliation(Support.optional),
    publisher_affiliation(Support.recommended),
    purge_nodes(Support.optional),
    retract_items(Support.optional),
    retrieve_affiliations(Support.recommended),
    retrieve_default(Support.recommended),
    retrieve_default_sub(Support.optional),
    retrieve_items(Support.recommended),
    retrieve_subscriptions(Support.recommended),
    subscribe(Support.required),
    subscription_options(Support.optional),
    subscriptions_notifications(Support.optional),
    instant_nodes(Support.recommended),
    filtered_notifications(Support.recommended);
    
    private final String feature;
    private final String qualifiedFeature;
    private final Support support;

    private PubSubFeature(Support support2) {
        this.feature = name().replace('_', '-');
        StringBuilder sb = new StringBuilder();
        sb.append("http://jabber.org/protocol/pubsub#");
        sb.append(this.feature);
        this.qualifiedFeature = sb.toString();
        this.support = support2;
    }

    public String getFeatureName() {
        return this.feature;
    }

    public String toString() {
        return this.qualifiedFeature;
    }

    public Support support() {
        return this.support;
    }

    public int length() {
        return this.qualifiedFeature.length();
    }

    public char charAt(int index) {
        return this.qualifiedFeature.charAt(index);
    }

    public CharSequence subSequence(int start, int end) {
        return this.qualifiedFeature.subSequence(start, end);
    }
}
