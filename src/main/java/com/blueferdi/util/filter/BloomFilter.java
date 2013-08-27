/*
 * Copyright (C) 2013 guang.com
 */
package com.blueferdi.util.filter;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author ferdi email ferdi@blueferdi.com
 * @version 0.0.1
 */
public class BloomFilter
{
    public final static int NUM_BITS = 8 * 102400;
    
    public static BigInteger computeQueryFilter(String... values)
    {
        BigInteger filter = BigInteger.ZERO;
        for (String s : values)
        {
            if (s != null && !"".equals(s.trim()))
            {
                filter = filter.or(computeBloomFilter(s));
            }
        }

        return filter;
    }

    public static BigInteger computeBloomFilter(byte[] b)
    {
        return FNV1Hash128.hash(b);
    }
    
    public static BigInteger computeBloomFilter(String s)
    {
        int cnt = s.length();
        if (cnt <= 0)
        {
            return BigInteger.ZERO;
        }

        BigInteger filter = BigInteger.ZERO;
        int bitpos = 0;

        BigInteger hash = FNV1Hash128.FNV_BASIS;
        for (int i = 0; i < cnt; i++)
        {
            char c = s.charAt(i);

            hash = hash.xor(BigInteger.valueOf(0xFF & c));
            hash = hash.multiply(FNV1Hash128.FNV_PRIME);

            hash = hash.xor(BigInteger.valueOf(0xFF & (c >> 8)));
            hash = hash.multiply(FNV1Hash128.FNV_PRIME);

            bitpos = hash.mod(BigInteger.valueOf(NUM_BITS)).intValue();
            if (bitpos < 0)
            {
                bitpos += NUM_BITS;
            }
            filter = filter.or(BigInteger.ONE.shiftLeft(bitpos));
        }

        return filter;
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        File file = new File("/home/ferdi/Downloads/ciku/八万.txt");
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        
        String line = "";
        
        HashSet<String> contents = new HashSet<String>();
        
        while((line = reader.readLine()) != null)
        {
            contents.add(line.split(" ")[0]);
        }
        
        String[] input = new String[contents.size()];
        
        contents.toArray(input);
        
        BigInteger filter = BloomFilter.computeQueryFilter(input);
        
//        File fileoutput = new File("words");
//        fileoutput.createNewFile();
//        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileoutput)));
//        
//        for(String s : input)
//        {
//            writer.write(s);
//            writer.newLine();
//        }
//        
//        writer.close();
        
        System.out.println(filter.toString(16)  + " " + filter.toByteArray().length + " " + input.length);
        reader.close();
        
        
        file = new File("/home/ferdi/Downloads/ciku/增量词库.txt");
        
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        
        List<String> contents_test = new ArrayList<String>();
        
        while((line = reader.readLine()) != null)
        {
            contents_test.add(line.split(" ")[0]);
        }
        
        input = new String[contents_test.size()];
        
        contents_test.toArray(input);
        
        int falsee = 0,falselu = 0;
        long filtercost = 0,lucost = 0,hashcost = 0,start = 0, stop = 0,start_inner = 0, stop_inner = 0;
        
        for(String s : input)
        {
            start = System.currentTimeMillis();
            
            //start_inner = System.currentTimeMillis();
            
            BigInteger filter_s = BloomFilter.computeQueryFilter(s);
            
            stop_inner = System.currentTimeMillis();
            
            hashcost += stop_inner - start;
            
            if(!filter.and(filter_s).equals(filter_s))
                falsee++;
            
            stop = System.currentTimeMillis();
            
            filtercost += stop - start;
            
            start = System.currentTimeMillis();
            
            if(!contents.contains(s))
                falselu++;
            
            stop = System.currentTimeMillis();
            
            lucost += stop - start;
            
        }
        
        System.out.println(falsee + ":" + falselu);
        
        System.out.println(filtercost + ":" + lucost + ":" + hashcost);
        
        reader.close();
        
        
    }
    
    
}
