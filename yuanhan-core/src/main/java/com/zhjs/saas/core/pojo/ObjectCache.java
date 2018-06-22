package com.yuanhan.yuanhan.core.pojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-07-30
 * @modified:	2017-07-30
 * @version:	
 */
public class ObjectCache
{
	private int cacheSize=0;
	private Map<String,Object> cacheObjects = null;
	private Map<String,HitInfo> hitInfoMap = null;
	
	public boolean isCached(String cacheKey){
		return cacheObjects.containsKey(cacheKey);
	}
	
	public ObjectCache(int cacheSize){
		this.cacheSize=cacheSize;
		this.cacheObjects = new HashMap<String,Object>(cacheSize);
		this.hitInfoMap=new HashMap<String,HitInfo>(cacheSize);
	}
	
	public Map<String,Object> getCacheObjects(){
		return cacheObjects;
	}
	
	public Object getCacheObject(String cacheKey){
		Object o = this.cacheObjects.get(cacheKey);
		HitInfo hitInfo=this.hitInfoMap.get(cacheKey);
		if(hitInfo!=null && o!=null){//更新访问时间和次数
			hitInfo.hitCount++;
			hitInfo.lastHitTime= new Date().getTime();
		}
		return o;
	}
	
	public void clear(int clearCount){
		List<HitInfo> l=new ArrayList<HitInfo>();
		l.addAll(hitInfoMap.values());
		Collections.sort(l);
		for(int i=0;i<clearCount;i++){
			HitInfo hitInfo=l.get(i);
			hitInfoMap.remove(hitInfo.key);
			cacheObjects.remove(hitInfo.key);
		}
	}
	
	public void setCache(String cacheKey,Object o){
		this.cacheObjects.put(cacheKey, o);
		HitInfo hitInfo = this.hitInfoMap.get(cacheKey);
		if(hitInfo==null){
			hitInfo= new HitInfo(cacheKey);
			this.hitInfoMap.put(cacheKey, hitInfo);
		}
		else{
			hitInfo.updateTime= new Date().getTime();
		}

		int i = hitInfoMap.size();
		if(i>cacheSize){
			clear(this.cacheSize/10);
		}
	}

	public void clear(){
		cacheObjects.clear();
		hitInfoMap.clear();
	}
	
	private class HitInfo implements Comparable<HitInfo> {
		String key=null;
		long hitCount=0l;
		long updateTime=0l;
		long lastHitTime=0l;
		
		public HitInfo(String cacheKey){
			key=cacheKey;
			hitCount = 1L;
			updateTime = new Date().getTime();
			lastHitTime = new Date().getTime();
		}

		public int compareTo(HitInfo hitInfo) {
			if(this.lastHitTime<hitInfo.lastHitTime){
				return -1;
			}
			else if(this.lastHitTime>hitInfo.lastHitTime){
				return 1;
			}
			else {
				if(this.hitCount<hitInfo.hitCount){
					return -1;
				}
				else if(this.hitCount>hitInfo.hitCount){
					return 1;
				}
				else {
					if(this.updateTime<hitInfo.updateTime){
						return -1;
					}
					else if(this.updateTime>hitInfo.updateTime){
						return 1;
					}
					else return 0;
				}
			}
		}
	}

}
