package com.hcmut.smarthome.utils.test;

import static org.junit.Assert.*;

import java.time.LocalTime;

import org.junit.Test;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import com.hcmut.smarthome.scenario.model.Condition;
import com.hcmut.smarthome.utils.ConstantUtil;

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
	
	@Test
	public void test(){
		System.out.println(EquationCaptcha("4x+4=20"));
	}
	
	private int EquationCaptcha(String E) {

		
		int R=0,Z=0,T=0,V=1,F=1;
		for(char c: E.toCharArray()){
			switch(c){
				case 'x':
//solving equation to x --> faktor to x will be used as divisor T					
					T=(Z==0)?F:Z;
					Z = 0;
// after x either a "+" or a "="  will follow or we are at the end of the equation
// End of equation --> we don't care what happens to the sign
// otherwise the "+" or "-" case will be run through next loopstep and will set the sign to the correct value
// Thus we don't care what it will be set to now --> no need to break
//					break;					
				case '-':
					F = -V;
					break;			
			
				case '=':
//  After "=" the sign needs to be inverted					
					V = -1;
//	After the = a new number (or x) will start --> we need to reset the sign to "normal"		
				case '+':
					F =  V;
					break;
			

				default:
					Z=Z*10+(c-48)*F;
					continue;
			}
			R+=Z;
			Z=0;			
		}
		
		R += Z;

	
		return R/-T;

}
	
	private String iqAddress(int n){
		return n > 1 ? iqAddress(-~n/2) + n%10.5 : "1";
	}
	
	private String BaseAdd(String c, int d, String e, int f) {
	    Long a = Long.valueOf(c, d), b = Long.valueOf(e, f);
	    return a.toString(a+b, a>b?d:f);
	}
	
	@Test
	public void testName() throws Exception {
		String content = "FromTo('09:00','10:00').action('TurnOn','light 7').endFromTo()";
		String regex = String.format("'%s'", "TurnOn");
		String replacementRegex = String.format("'%s'", "light 3");
		String replace = content.replaceAll(regex, replacementRegex);
		System.out.println(replace);
		System.out.println(ConstantUtil.ALL_DEVICE_ACTIONS);
	}
}
