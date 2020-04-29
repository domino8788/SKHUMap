package com.domino.skhumap.dto

import com.domino.skhumap.Facility

data class SearchableFacility(val department: Facility, val floorNumber:Int, val facility:Facility){}