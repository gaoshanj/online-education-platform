package com.example.onlineedu.domain.dto;

import lombok.Data;

/**
 * 评分-数量聚合结果 DTO（供评分分布饼图使用）
 *
 * @author feng
 */
@Data
public class RatingCountDTO {
    /** 星级（1-5） */
    private Integer rating;
    /** 该星级数量 */
    private Integer count;
}
