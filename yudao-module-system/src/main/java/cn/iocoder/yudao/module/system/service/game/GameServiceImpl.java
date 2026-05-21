package cn.iocoder.yudao.module.system.service.game;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.controller.admin.game.vo.GamePageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.game.vo.GameSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.game.GameDO;
import cn.iocoder.yudao.module.system.dal.mysql.game.GameMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.GAME_NOT_FOUND;

@Service
@Validated
public class GameServiceImpl implements GameService {

    @Resource
    private GameMapper gameMapper;

    @Override
    public Long createGame(GameSaveReqVO createReqVO) {
        GameDO game = BeanUtils.toBean(createReqVO, GameDO.class);
        gameMapper.insert(game);
        return game.getId();
    }

    @Override
    public void updateGame(GameSaveReqVO updateReqVO) {
        validateGameExists(updateReqVO.getId());
        GameDO updateObj = BeanUtils.toBean(updateReqVO, GameDO.class);
        gameMapper.updateById(updateObj);
    }

    @Override
    public void deleteGame(Long id) {
        validateGameExists(id);
        gameMapper.deleteById(id);
    }

    @Override
    public void deleteGameList(List<Long> ids) {
        gameMapper.deleteByIds(ids);
    }

    @Override
    public GameDO getGame(Long id) {
        return gameMapper.selectById(id);
    }

    @Override
    public PageResult<GameDO> getGamePage(GamePageReqVO pageReqVO) {
        return gameMapper.selectPage(pageReqVO);
    }

    @Override
    public List<GameDO> getGameList(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return gameMapper.selectByIds(ids);
    }

    private void validateGameExists(Long id) {
        if (id == null) {
            return;
        }
        if (gameMapper.selectById(id) == null) {
            throw exception(GAME_NOT_FOUND);
        }
    }

}
