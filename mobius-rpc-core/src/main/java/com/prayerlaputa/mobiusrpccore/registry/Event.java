package com.prayerlaputa.mobiusrpccore.registry;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author:
 * @description:
 * @date: 2024/4/5 21:37
 * @version: 1.0
 */
@AllArgsConstructor
@Data
public class Event {
    List<String> data;
}
