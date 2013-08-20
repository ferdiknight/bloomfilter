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
    public final static int NUM_BITS = 8 * 300;
    
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
        File file = new File("F:/words.txt");
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        
        String line = "";
        
        HashSet<String> contents = new HashSet<String>();
        
        while((line = reader.readLine()) != null)
        {
            contents.addAll(Arrays.asList(line.split(" ")));
        }
        
        String[] input = new String[contents.size()];
        
        contents.toArray(input);
        
        BigInteger filter = BloomFilter.computeQueryFilter(input);
        
        System.out.println(filter.toString(16)  + " " + filter.toByteArray().length + " " + input.length);
        reader.close();
        
        
        file = new File("F:/testwords.txt");
        
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        
        List<String> contents_test = new ArrayList<String>();
        
        while((line = reader.readLine()) != null)
        {
            contents_test.addAll(Arrays.asList(line.split(" ")));
        }
        
        input = new String[contents_test.size()];
        
        contents_test.toArray(input);
        
        for(String s : input)
        {
            BigInteger filter_s = BloomFilter.computeQueryFilter(s);
            if(!filter.and(filter_s).equals(filter_s))
                System.out.println("false");
        }
        
        reader.close();
        
        
    }
    
    
}
