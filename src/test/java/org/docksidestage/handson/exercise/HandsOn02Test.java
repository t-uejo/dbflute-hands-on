package org.docksidestage.handson.exercise;

import java.time.LocalDate;

import javax.annotation.Resource;

import org.dbflute.cbean.result.ListResultBean;
import org.docksidestage.handson.dbflute.exbhv.MemberBhv;
import org.docksidestage.handson.dbflute.exentity.Member;
import org.docksidestage.handson.unit.UnitContainerTestCase;

public class HandsOn02Test extends UnitContainerTestCase{

    @Resource
    private MemberBhv memberBhv;

    public void test_existsTestData() throws Exception {
        // ## Arrange ##

        // ## Act ##
    	int count = memberBhv.selectCount(cb -> {});

        // ## Assert ##
    	assertTrue(count > 0);
    }
    
    /**
     * 会員名称がSで始まる会員を検索 (これはタイトル、この中にも要件が含まれている)
     * o 会員名称の昇順で並べる (これは実装要件、Arrange でこの通りに実装すること)
     * o (検索結果の)会員名称がSで始まっていることをアサート (これはアサート要件、Assert でこの通りに実装すること)
     * o 該当テストデータがない、条件間違い、などで検索結果０件による素通りgreenにならないように (今後ずっと同じ)
     */
    public void test_1() throws Exception {
        // ## Arrange ##
        String prefix = "S";

        // ## Act ##
        ListResultBean<Member> memberList = memberBhv.selectList(cb -> {
            cb.query().setMemberName_LikeSearch(prefix, op -> op.likePrefix());
            cb.query().addOrderBy_MemberName_Asc();
        });
        
        // ## Assert ##
        assertHasAnyElement(memberList);
        memberList.forEach(member -> {
            String memberName = member.getMemberName();
            log("memberName: {}", memberName); // yon can use Slf4j-like placeholder
            assertTrue(memberName.startsWith(prefix));
        });
    }
    
    /**
     * 会員IDが1の会員を検索
     * o 一件検索として検索すること
     * o 会員IDが 1 であることをアサート
     */
    public void test_2() throws Exception {
        // ## Arrange ##
        // ## Act ##
        memberBhv.selectEntity(cb -> cb.acceptPK(1)).alwaysPresent(member -> {
            // ## Assert ##
            Integer memberId = member.getMemberId();
            log("memberId: {}", memberId);
            assertEquals(1, memberId);
        });
    }
    
    /**
     * 生年月日がない会員を検索
     * - 更新日時の降順で並べる
     * - 生年月日がないことをアサート
     */
    public void test_3() throws Exception {
        // ## Arrange ##
        // ## Act ##
        ListResultBean<Member> memberList = memberBhv.selectList(cb -> {
            cb.query().setBirthdate_IsNull();
            cb.query().addOrderBy_UpdateDatetime_Desc();
        });

        // ## Assert ##
        assertHasAnyElement(memberList);
        memberList.forEach(member -> {
            LocalDate birthdate = member.getBirthdate();
            log(member.getMemberName(), birthdate); // comma-separated if no template string
            assertNull(birthdate);
        });
    }
}
