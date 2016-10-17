package com.hcmut.smarthome.utils.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

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
}
