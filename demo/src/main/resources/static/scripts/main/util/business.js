(function (window, undefined) {
    var Business = Base.createClass('main.util.Business');
    var Action = Base.getClass('main.util.Action');

    $.extend(Business, {
        followUser: fFollowUser,
        followQuestion: fFollowQuestion
    });

    function fFollowUser() {
        $(document).on('click', '.js-follow-user', function (oEvent) {
            var oEl = $(oEvent.currentTarget);
            var sId = $.trim(oEl.attr('data-id'));
            if (!sId) {
                return;
            }
            // 禁止频繁点击
            if (oEl.attr('data-limit')) {
                return;
            }
            oEl.attr('data-limit', '1');
            var bFollow = oEl.attr('data-status') === '1';
            alert('出现错误'+bFollow+sId);
            Action[bFollow ? 'unFollowUser' : 'followUser']({
                userId: sId,
                call: function (oResult) {
                    // 修改标记位

                    oEl.attr('data-status', bFollow ? '0' : '1');
                    // 按钮颜色
                    oEl.removeClass('zg-btn-follow zg-btn-unfollow').addClass(bFollow ? 'zg-btn-follow' : 'zg-btn-unfollow');
                    // 文字
                    oEl.html(bFollow ? '关注' : '取消关注');
                },
                error: function (oResult) {
                    alert('出现错误，请重试'+oEl.attr('data-id'));
                },
                always: function () {
                    oEl.removeAttr('data-limit');
                }
            });
        });
    }

    function fFollowQuestion(oConf) {
        var that = this;
        var oCountEl = $(oConf.countEl);
        var oListEl = $(oConf.listEl);
        $(document).on('click', '.js-follow-question', function (oEvent) {
            var oEl = $(oEvent.currentTarget);
            var sId = $.trim(oEl.attr('data-id'));
            if (!sId) {
                return;
            }
            // 禁止频繁点击
            if (oEl.attr('data-limit')) {
                return;
            }
            oEl.attr('data-limit', '1');
            var bFollow = oEl.attr('data-status') === '1';
            Action[bFollow ? 'unFollowQuestion' : 'followQuestion']({
                questionId: sId,
                call: function (oResult) {
                    // 修改标记位
                    oEl.attr('data-status', bFollow ? '0' : '1');
                    // 按钮颜色
                    oEl.removeClass('zg-btn-white zg-btn-green').addClass(bFollow ? 'zg-btn-green' : 'zg-btn-white');
                    // 文字
                    oEl.html(bFollow ? '关注问题' : '取消关注');
                    // 修改数量
                    oCountEl.html(oResult.count);
                    if (bFollow) {
                        // 移除用户
                        oListEl.find('.js-user-' + oResult.userId).remove();
                    } else {
                        // 显示用户
                        //？？点击之后不会马上更新
                        alert(oResult.userId);//原本这是 oResult.id，但是输出oResult.id是空的，经过逻辑判断发现，oResult是从json里面获取数据的，而我自己定义的id名是userId，所以传过来的json有userId，而没有id
                        oListEl.prepend('<a class="zm-item-link-avatar js-user-' + oResult.userId + '" href="/user/' + oResult.userId + '" data-original_title="' + oResult.name + '"><img src="' + oResult.headUrl + '" class="zm-item-img-avatar"></a>');
                    }
                },
                error: function (oResult) {
                    alert('出现错误，请重试');
                },
                always: function () {
                    oEl.removeAttr('data-limit');
                }
            });
        });
    }
})(window);