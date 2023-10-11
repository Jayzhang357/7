package com.zhd.bd970.manage.interfaces;

import java.util.HashMap;
import java.util.List;

import com.zhd.gps.manage.models.TrackStatEntity;

public interface ReceiveTrackStatListner {

	public void TellReceiveTrackStat(HashMap<Integer, TrackStatEntity> trackStatMap);
}
