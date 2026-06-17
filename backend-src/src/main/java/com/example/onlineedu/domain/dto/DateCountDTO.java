package com.example.onlineedu.domain.dto;

import lombok.Data;

/**
 * 日期-数量聚合结果 DTO（供折线图使用）
 *
 * @author feng
 */
@Data
public class DateCountDTO {
    /** 日期字符串（yyyy-MM-dd） */
    private String date;
    /** 对应数量 */
    private Integer count;
}
