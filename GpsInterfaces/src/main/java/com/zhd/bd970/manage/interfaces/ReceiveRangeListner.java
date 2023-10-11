package com.zhd.bd970.manage.interfaces;

import java.util.HashMap;
import java.util.List;

import com.zhd.gps.manage.models.RangeEntity;
import com.zhd.gps.manage.models.TrackStatEntity;

public interface ReceiveRangeListner {

	public void TellReceiveRange(HashMap<Integer, RangeEntity> rangeMap);
}
