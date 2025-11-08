package com.group.SeatIn.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;

import com.group.SeatIn.model.Tier;
import com.group.SeatIn.model.Event;
import java.util.List;

public interface TierRepository extends JpaRepository<Tier,Long> {

    /**
     * Method will just match Event with Tier since it is an attribute of Tier
     * @param event
     * @return all Tiers associated
     */
    List<Tier> findTiersByEvent(Event event);
    Tier findByEvent_EventIdAndTierName(Long eventId, String tierName);


}
