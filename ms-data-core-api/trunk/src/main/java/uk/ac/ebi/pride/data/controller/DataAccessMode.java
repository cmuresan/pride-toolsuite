package uk.ac.ebi.pride.data.controller;

/**
 * DataAccessMode is mainly to aid caching in data access controller
 * There are two modes at the moment:
 * <p/>
 * 1. CACHE_ONLY means only retrieve information from the cache.
 * <p/>
 * 2. CACHE_AND_SOURCE means retrieve information from cache first, if didn't find anything,
 * then read from data source directly.
 * <p/>
 * User: rwang
 * Date: 07-Sep-2010
 * Time: 16:36:09
 */
public enum DataAccessMode { CACHE_ONLY, CACHE_AND_SOURCE, }



