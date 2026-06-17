package com.example.onlineedu.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体类
 * 包含所有表的公共字段
 *
 * @author feng
 */
@Data
public class BaseEntity implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 主键ID
         */
        @TableId(value = "id", type = IdType.AUTO)
        private Long id;

        /**
         * 创建时间
         */
        @TableField(value = "create_time", fill = FieldFill.INSERT)
        private LocalDateTime createTime;

        /**
         * 更新时间
         */
        @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
        private LocalDateTime updateTime;

        /**
         * 逻辑删除标记：0-未删除，1-已删除
         */
        @TableLogic
        @TableField("is_deleted")
        private Integer isDeleted;
}
