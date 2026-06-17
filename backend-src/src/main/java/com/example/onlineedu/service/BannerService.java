package com.example.onlineedu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.domain.dto.BannerDTO;
import com.example.onlineedu.domain.vo.BannerVO;

import java.util.List;

/**
 * 轮播图服务接口
 *
 * @author feng
 */
public interface BannerService {

    /**
     * 查询启用状态的轮播图列表（App端）
     *
     * @return 启用的轮播图列表（按 sort 升序）
     */
    List<BannerVO> listEnabledBanners();

    /**
     * 分页查询轮播图（管理端）
     *
     * @param page 当前页
     * @param size 每页大小
     * @return 分页结果
     */
    Page<BannerVO> pageBanners(Integer page, Integer size);

    /**
     * 新增轮播图（管理端）
     *
     * @param dto 轮播图信息
     */
    void addBanner(BannerDTO dto);

    /**
     * 修改轮播图（管理端）
     *
     * @param id  轮播图ID
     * @param dto 轮播图信息
     */
    void updateBanner(Long id, BannerDTO dto);

    /**
     * 删除轮播图（管理端，逻辑删除）
     *
     * @param id 轮播图ID
     */
    void deleteBanner(Long id);

    /**
     * 启用轮播图（管理端）
     *
     * @param id 轮播图ID
     */
    void enableBanner(Long id);

    /**
     * 禁用轮播图（管理端）
     *
     * @param id 轮播图ID
     */
    void disableBanner(Long id);
}
