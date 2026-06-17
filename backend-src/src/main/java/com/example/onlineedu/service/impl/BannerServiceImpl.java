package com.example.onlineedu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.common.ErrorCode;
import com.example.onlineedu.domain.dto.BannerDTO;
import com.example.onlineedu.domain.entity.BannerEntity;
import com.example.onlineedu.domain.entity.CourseEntity;
import com.example.onlineedu.domain.vo.BannerVO;
import com.example.onlineedu.exception.BusinessException;
import com.example.onlineedu.mapper.BannerMapper;
import com.example.onlineedu.mapper.CourseMapper;
import com.example.onlineedu.service.BannerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 轮播图服务实现类
 *
 * @author feng
 */
@Slf4j
@Service
public class BannerServiceImpl implements BannerService {

    @Autowired
    private BannerMapper bannerMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public List<BannerVO> listEnabledBanners() {
        LambdaQueryWrapper<BannerEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BannerEntity::getStatus, 1)
                .orderByAsc(BannerEntity::getSort);
        List<BannerEntity> banners = bannerMapper.selectList(wrapper);
        return banners.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public Page<BannerVO> pageBanners(Integer page, Integer size) {
        LambdaQueryWrapper<BannerEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(BannerEntity::getSort)
               .orderByDesc(BannerEntity::getCreateTime);
        Page<BannerEntity> entityPage = bannerMapper.selectPage(
                new Page<>(page, size), wrapper);

        Page<BannerVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(entityPage.getRecords().stream().map(this::toVO).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public void addBanner(BannerDTO dto) {
        BannerEntity banner = new BannerEntity();
        BeanUtils.copyProperties(dto, banner);
        banner.setCreateTime(LocalDateTime.now());
        banner.setUpdateTime(LocalDateTime.now());
        banner.setIsDeleted(0);
        bannerMapper.insert(banner);
        log.info("新增轮播图成功：{}", banner.getTitle());
    }

    @Override
    public void updateBanner(Long id, BannerDTO dto) {
        BannerEntity banner = bannerMapper.selectById(id);
        if (banner == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "轮播图不存在");
        }
        BeanUtils.copyProperties(dto, banner);
        banner.setUpdateTime(LocalDateTime.now());
        bannerMapper.updateById(banner);
        log.info("修改轮播图成功：{}", banner.getTitle());
    }

    @Override
    public void deleteBanner(Long id) {
        BannerEntity banner = bannerMapper.selectById(id);
        if (banner == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "轮播图不存在");
        }
        bannerMapper.deleteById(id);
        log.info("删除轮播图成功，id：{}", id);
    }

    @Override
    public void enableBanner(Long id) {
        updateStatus(id, 1);
    }

    @Override
    public void disableBanner(Long id) {
        updateStatus(id, 0);
    }

    // ======================== private ========================

    private void updateStatus(Long id, int status) {
        BannerEntity banner = bannerMapper.selectById(id);
        if (banner == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "轮播图不存在");
        }
        banner.setStatus(status);
        banner.setUpdateTime(LocalDateTime.now());
        bannerMapper.updateById(banner);
    }

    private BannerVO toVO(BannerEntity entity) {
        BannerVO vo = new BannerVO();
        BeanUtils.copyProperties(entity, vo);
        if (entity.getCourseId() != null) {
            CourseEntity course = courseMapper.selectById(entity.getCourseId());
            if (course != null) {
                vo.setCourseName(course.getCourseName());
            }
        }
        return vo;
    }
}
