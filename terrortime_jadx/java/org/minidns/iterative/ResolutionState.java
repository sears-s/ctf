package org.minidns.iterative;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.minidns.dnsmessage.DnsMessage;
import org.minidns.dnsmessage.Question;
import org.minidns.iterative.IterativeClientException.LoopDetected;
import org.minidns.iterative.IterativeClientException.MaxIterativeStepsReached;

public class ResolutionState {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final HashMap<InetAddress, Set<Question>> map = new HashMap<>();
    private final IterativeDnsClient recursiveDnsClient;
    private int steps;

    ResolutionState(IterativeDnsClient recursiveDnsClient2) {
        this.recursiveDnsClient = recursiveDnsClient2;
    }

    /* access modifiers changed from: 0000 */
    public void recurse(InetAddress address, DnsMessage query) throws LoopDetected, MaxIterativeStepsReached {
        Question question = query.getQuestion();
        if (!this.map.containsKey(address)) {
            this.map.put(address, new HashSet());
        } else if (((Set) this.map.get(address)).contains(question)) {
            throw new LoopDetected(address, question);
        }
        int i = this.steps + 1;
        this.steps = i;
        if (i <= this.recursiveDnsClient.maxSteps) {
            boolean add = ((Set) this.map.get(address)).add(question);
            return;
        }
        throw new MaxIterativeStepsReached();
    }

    /* access modifiers changed from: 0000 */
    public void decrementSteps() {
        this.steps--;
    }
}
