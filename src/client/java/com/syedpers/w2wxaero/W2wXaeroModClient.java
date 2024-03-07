package com.syedpers.w2wxaero;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.math.BlockPos;
import xaero.common.XaeroMinimapSession;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.minimap.waypoints.WaypointsManager;
import xaero.common.settings.ModSettings;
import xaero.minimap.XaeroMinimap;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.event.WaystoneActivatedEvent;
import net.blay09.mods.waystones.api.event.WaystoneInitializedEvent;
import net.blay09.mods.waystones.api.event.WaystoneRemovedEvent;
import net.blay09.mods.waystones.api.event.WaystoneUpdateReceivedEvent;
import net.blay09.mods.waystones.api.event.WaystoneUpdatedEvent;

import java.util.ArrayList;
import java.util.List;

public class W2wXaeroModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		Balm.getEvents().onEvent(WaystoneActivatedEvent.class, this::onWaystoneActivated);
		Balm.getEvents().onEvent(WaystoneUpdatedEvent.class, this::onWaystoneUpdated);
		Balm.getEvents().onEvent(WaystoneRemovedEvent.class, this::onWaystoneRemoved);
		//WaystonesListReceivedEvent
		//WaystonesRemoveReceivedEvent
		//WaystonesUpdateReceivedEvent
	}

	public void onWaystoneActivated(WaystoneActivatedEvent waystoneActivatedEvent) {
		try {
			XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
			if (minimapSession == null) return;
			WaypointsManager waypointsManager = minimapSession.getWaypointsManager();
			BlockPos pos=waystoneActivatedEvent.getWaystone().getPos();
			String name = waystoneActivatedEvent.getWaystone().getName().getString();
			if (name == null || name.isEmpty()) name = "Untitled Waystone";
			Waypoint waypoint = new Waypoint(pos.getX(), pos.getY()+2, pos.getZ(), name, name.substring(0,1),(int)(Math.random()*ModSettings.ENCHANT_COLORS.length));
			waypointsManager.getWaypoints().getList().add(waypoint);
			XaeroMinimap.instance.getSettings().saveWaypoints(waypointsManager.getCurrentWorld());
		} catch(final Exception e) {
		}
	}

	public void onWaystoneRemoved(WaystoneRemovedEvent waystoneRemovedEvent) {
		try {
			XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
			if (minimapSession == null) return;
			WaypointsManager waypointsManager = minimapSession.getWaypointsManager();
			Waypoint waypoint = getWaypoint(waypointsManager.getWaypoints().getList(), waystoneRemovedEvent.getWaystone());
			waypointsManager.getWaypoints().getList().remove(waypoint);
			XaeroMinimap.instance.getSettings().saveWaypoints(waypointsManager.getCurrentWorld());
		} catch(final Exception e) {
		}
	}

	public void onWaystoneUpdated(WaystoneUpdatedEvent waystoneUpdatedEvent) {
		try {
			XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
			if (minimapSession == null) return;
			WaypointsManager waypointsManager = minimapSession.getWaypointsManager();
			Waystone waystone = waystoneUpdatedEvent.getWaystone();
			BlockPos pos=waystone.getPos();
			String name = waystone.getName().getString();
			if (name == null || name.isEmpty()) name = "Untitled Waystone";
			Waypoint waypoint = getWaypoint(waypointsManager.getWaypoints().getList(), waystone);
			if (waypoint == null) {
				waypoint = new Waypoint(pos.getX(), pos.getY()+2, pos.getZ(), name, name.substring(0,1),(int)(Math.random()*ModSettings.ENCHANT_COLORS.length));
				waypointsManager.getWaypoints().getList().add(waypoint);
			} else {
				waypoint.setName(name);
				waypoint.setSymbol(name.substring(0, 1));
				waypoint.setX(pos.getX());
				waypoint.setY(pos.getY() + 2);
				waypoint.setZ(pos.getZ());
			}
			XaeroMinimap.instance.getSettings().saveWaypoints(waypointsManager.getCurrentWorld());
		} catch(final Exception e) {
		}
	}

	private Waypoint getWaypoint(ArrayList<Waypoint> waypoints, Waystone waystone){
        BlockPos waystonePos = waystone.getPos();
        for(Waypoint waypoint : waypoints){
            if(waypoint.getX() == waystonePos.getX() && waypoint.getY() == waystonePos.getY() + 2 && waypoint.getZ() == waystonePos.getZ()){
                return waypoint;
            }
        }
        return null;
    }
 }