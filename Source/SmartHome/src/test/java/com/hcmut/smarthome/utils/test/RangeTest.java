package com.hcmut.smarthome.utils.test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalTime;

import org.junit.Test;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import com.hcmut.smarthome.scenario.model.Condition;

public class RangeTest {
	@Test
	public void testCase1() throws Exception {
		RangeSet<Integer> rs = TreeRangeSet.create();
		rs.add(Range.all());
		
		rs.remove(Range.singleton(5));
		
		System.out.println(rs);
		
		rs = rs.subRangeSet(Range.atLeast(3));
		
		System.out.println(rs);
	}
	
	@Test
	public void testCase2() throws Exception {
		RangeSet<Integer> rs = TreeRangeSet.create();
		rs.add(Range.all());
		
		rs.remove(Range.singleton(5));
		
		System.out.println(rs);
		
		rs.remove(Range.singleton(3));
		
		rs = rs.subRangeSet(Range.closed(1, 10));
		
		System.out.println(rs);
		
		rs.remove(Range.singleton(4));
		
		System.out.println(rs);
	}
	
	@Test
	public void testCase3() throws Exception {
		Range<Integer> r1 = Range.closedOpen(35, 35);
		System.out.println(r1.isEmpty());
		
	}
	
	@Test
	public void testCase4() throws Exception {
		LocalTime t1 = LocalTime.of(15,45);
		LocalTime t2 = LocalTime.of(23,45);
		LocalTime now = LocalTime.now();
		if( now.isBefore(t2) && now.isAfter(t1))			
		System.out.println(t1.plusHours(9));
		System.out.println(t1);
		
		Range<LocalTime> range = Range.closed(t1, t2);
		System.out.println(range.contains(now));
	}
	
	@Test
	public void testCase5() throws Exception {
		Condition<LocalTime> c = new Condition<>();
		c.setRange(Range.closed(LocalTime.of(5, 40), LocalTime.of(6, 40)));
		
		Condition cc = c;
	}
}
