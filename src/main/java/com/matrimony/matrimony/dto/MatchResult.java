package com.matrimony.matrimony.dto;

import com.matrimony.matrimony.entity.Profile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MatchResult {
    private Profile profile;
    private double score; // percentage 0–100
}
