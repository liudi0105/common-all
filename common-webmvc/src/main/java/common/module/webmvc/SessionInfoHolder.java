package common.module.webmvc;


public class SessionInfoHolder {
    private static final ThreadLocal<SessionInfo> SESSION_HOLDER = ThreadLocal.withInitial(SessionInfo::new);

    protected static void setSession(SessionInfo info) {
        SESSION_HOLDER.set(info);
    }

    protected static SessionInfo getSession() {
        return SESSION_HOLDER.get();
    }
}
