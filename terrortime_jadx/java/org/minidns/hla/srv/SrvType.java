package org.minidns.hla.srv;

public enum SrvType {
    xmpp_client(SrvService.xmpp_client, SrvProto.tcp),
    xmpp_server(SrvService.xmpp_server, SrvProto.tcp);
    
    public final SrvProto proto;
    public final SrvService service;

    private SrvType(SrvService service2, SrvProto proto2) {
        this.service = service2;
        this.proto = proto2;
    }
}
