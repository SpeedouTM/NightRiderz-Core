package com.soapboxrace.core.bo;

import com.soapboxrace.core.xmpp.OpenFireRestApiCli;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class PresenceManager
{
    @EJB
    private OpenFireRestApiCli openfire;

    private final Map<Long, Integer> presenceMap;

    public PresenceManager()
    {
        presenceMap = new HashMap<>();
    }

    public void setPresence(long personaId, int presence)
    {
        presenceMap.put(personaId, presence);
    }

    public void removePresence(long personaId)
    {
        presenceMap.remove(personaId);
    }

    public int getPresence(long personaId)
    {
        return presenceMap.getOrDefault(personaId, 0);
    }

    @Schedule(minute = "*/5", hour = "*", persistent = false)
    public void removeOldPersonas() {
        List<Long> personas = openfire.getOnlinePersonas();
        presenceMap.keySet().removeIf(persona -> !personas.contains(persona));
    }
}