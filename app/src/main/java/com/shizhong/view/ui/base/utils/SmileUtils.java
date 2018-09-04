/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.shizhong.view.ui.base.utils;

import android.content.Context;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.style.ImageSpan;

import com.shizhong.view.ui.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SmileUtils {
//    public static final String ee_1 = "[):]";
//    public static final String ee_2 = "[:D]";
//    public static final String ee_3 = "[;)]";
//    public static final String ee_4 = "[:-o]";
//    public static final String ee_5 = "[:p]";
//    public static final String ee_6 = "[(H)]";
//    public static final String ee_7 = "[:@]";
//    public static final String ee_8 = "[:s]";
//    public static final String ee_9 = "[:$]";
//    public static final String ee_10 = "[:(]";
//    public static final String ee_11 = "[:'(]";
//    public static final String ee_12 = "[:|]";
//    public static final String ee_13 = "[(a)]";
//    public static final String ee_14 = "[8o|]";
//    public static final String ee_15 = "[8-|]";
//    public static final String ee_16 = "[+o(]";
//    public static final String ee_17 = "[<o)]";
//    public static final String ee_18 = "[|-)]";
//    public static final String ee_19 = "[*-)]";
//    public static final String ee_20 = "[:-#]";
//    public static final String ee_21 = "[:-*]";
//    public static final String ee_22 = "[^o)]";
//    public static final String ee_23 = "[8-)]";
//    public static final String ee_24 = "[(|)]";
//    public static final String ee_25 = "[(u)]";
//    public static final String ee_26 = "[(S)]";
//    public static final String ee_27 = "[(*)]";
//    public static final String ee_28 = "[(#)]";
//    public static final String ee_29 = "[(R)]";
//    public static final String ee_30 = "[({)]";
//    public static final String ee_31 = "[(})]";
//    public static final String ee_32 = "[(k)]";
//    public static final String ee_33 = "[(F)]";
//    public static final String ee_34 = "[(W)]";
//    public static final String ee_35 = "[(D)]";



//    public static final String ee_1 = "[::)]";//微笑
//    public static final String ee_2 = "[::~]";//撇嘴
//    public static final String ee_3 = "[::B]";//色
//    public static final String ee_4 = "[::\\]";//发呆
//    public static final String ee_5 = "[:8-)]";//得意
//    public static final String ee_6 = "[::<]";//流泪
//    public static final String ee_7 = "[::$]";//害羞
//    public static final String ee_8 = "[::X]";//闭嘴
//    public static final String ee_9 = "[::Z]";//睡
////    public static final String ee_10 = "[::'(]";//大哭
////    public static final String ee_11 = "[::-|]";//尴尬
//    public static final String ee_10 = "[::@]";//发怒
//    public static final String ee_11 = "[::P]";//调皮
//    public static final String ee_12 = "[::D]";//呲牙
//    public static final String ee_13 = "[::O]";//惊讶
//    public static final String ee_14 = "[::(]";//难过
////    public static final String ee_17 = "[::+]";//酷
////    public static final String ee_18 = "[:-b]";//冷汗
////    public static final String ee_19 = "[::Q]";//抓狂
////    public static final String ee_20 = "[::T]";//吐
////    public static final String ee_21 = "[:,@P]";//偷笑
//    public static final String ee_15 = "[:,@-D]";//愉快
//    public static final String ee_16 = "[::d]";//白眼
//    public static final String ee_17 = "[:,@o]";//傲慢
////    public static final String ee_25 = "[::g]";//饥饿
//    public static final String ee_18 = "[:|-)]";//困
//    public static final String ee_19 = "[::!]";//惊恐
//    public static final String ee_20 = "[::L]";//流汗
//    public static final String ee_21 = "[::>]";//憨笑
////    public static final String ee_30 = "[::,@]";//悠闲
////    public static final String ee_31 = "[:,@f]";//奋斗
////    public static final String ee_32 = "[::-s]";//咒骂
////    public static final String ee_33 = "[:？]";//疑问
////    public static final String ee_34 = "[:,@x]";//嘘
//    public static final String ee_22 = "[:,@@]";//晕
//    public static final String ee_23 = "[::8]";//疯啦
////    public static final String ee_37 = "[:,@!]";//衰
////    public static final String ee_38 = "[:!!!]";//骷髅
////    public static final String ee_39 = "[:xx]";//敲打
//    public static final String ee_24 = "[:bye]";//再见
//    public static final String ee_25 = "[:wipe]";//擦汗
////    public static final String ee_42 = "[:dig]";//抠鼻
//    public static final String ee_26 = "[:handclap]";//鼓掌
////    public static final String ee_44 = "[:&-(]";//糗大了
//    public static final String ee_27 = "[:B-)]";//坏
////    public static final String ee_46 = "[:<@]";//左哼哼
////    public static final String ee_47 = "[:@>]";//右哼哼
////    public static final String ee_48 = "[::-O]";//哈欠
////    public static final String ee_49 = "[:>-|]";//鄙视
////    public static final String ee_50 = "[:P-(]";//委屈
//    public static final String ee_28 = "[::'|]";//快哭了
//    public static final String ee_29 = "[:x-)]";//阴险
//    public static final String ee_30 = "[:::*]";//亲亲
////    public static final String ee_54 = "[:@x]";//吓
////    public static final String ee_55 = "[:8*]";//可怜
////    public static final String ee_56 = "[:pd]";//菜刀
////    public static final String ee_57 = "[:<W>]";//西瓜
////    public static final String ee_58 = "[:bear]";//啤酒
////    public static final String ee_59 = "[:basketb]";//篮球
////    public static final String ee_60 = "[:oo]";//兵乓
////    public static final String ee_61 = "[:coffee]";//咖啡
////    public static final String ee_62 = "[:eat]";//饭
////    public static final String ee_63 = "[:pig]";//猪头
//    public static final String ee_31 = "[:rose]";//玫瑰
////    public static final String ee_65 = "[:fade]";//凋谢
//    public static final String ee_32 = "[:showlove]";//嘴唇
//    public static final String ee_33 = "[:heart]";//爱心
//    public static final String ee_34 = "[:break]";//心碎
////    public static final String ee_69 = "[:cake]";//蛋糕
////    public static final String ee_70 = "[:li]";//闪电
//    public static final String ee_35= "[:bome]";//炸弹
////    public static final String ee_72 = "[:kn]";//刀
////    public static final String ee_73 = "[:footb]";//足球
////    public static final String ee_74 = "[:ladybug]";//瓢虫
////    public static final String ee_75 = "[:shit]";//便便
//    public static final String ee_36 = "[:moom]";//月亮
//    public static final String ee_37 = "[:sun]";//太阳
//    public static final String ee_38 = "[:gift]";//礼物
////    public static final String ee_79 = "[:hug]";//拥抱
//    public static final String ee_39 = "[:strong]";//强
//    public static final String ee_40 = "[:weak]";//弱
////    public static final String ee_82 = "[:share]";//握手
//    public static final String ee_41 = "[;v]";//胜利
////    public static final String ee_84 = "[:@)]";//抱拳
////    public static final String ee_85 = "[:jj]";//勾引
//    public static final String ee_42 = "[:@@]";//拳头
////    public static final String ee_87 = "[:bad]";//差劲
////    public static final String ee_88 = "[:lvu]";//爱你
//    public static final String ee_43 = "[:no]";//NO
//    public static final String ee_44 = "[:ok]";//OK
public static final String ee_1 = "[微笑]";//
    public static final String ee_2 = "[撇嘴]";//
    public static final String ee_3 = "[色]";//
    public static final String ee_4 = "[发呆]";//
    public static final String ee_5 = "[得意]";//
    public static final String ee_6 = "[流泪]";//
    public static final String ee_7 = "[害羞]";//
    public static final String ee_8 = "[闭嘴]";//
    public static final String ee_9 = "[睡]";//
    public static final String ee_10 = "[大哭]";//
    public static final String ee_11 = "[尴尬]";//
    public static final String ee_12 = "[发怒]";//
    public static final String ee_13 = "[调皮]";//
    public static final String ee_14 = "[呲牙]";//
    public static final String ee_15 = "[惊讶]";//
    public static final String ee_16 = "[难过]";//
    public static final String ee_17 = "[酷]";//
    public static final String ee_18 = "[冷汗]";//
    public static final String ee_19 = "[抓狂]";//
    public static final String ee_20 = "[吐]";//
    public static final String ee_21 = "[偷笑]";//
    public static final String ee_22 = "[愉快]";//
    public static final String ee_23 = "[白眼]";//
    public static final String ee_24 = "[傲慢]";//
    public static final String ee_25 = "[饥饿]";//
    public static final String ee_26 = "[困]";//
    public static final String ee_27 = "[惊恐]";//
    public static final String ee_28 = "[流汗]";//
    public static final String ee_29 = "[憨笑]";//
    public static final String ee_30 = "[悠闲]";//
    public static final String ee_31 = "[奋斗]";//
    public static final String ee_32 = "[咒骂]";//
    public static final String ee_33 = "[疑问]";//
    public static final String ee_34 = "[嘘]";//
    public static final String ee_35 = "[晕]";//
    public static final String ee_36 = "[疯啦]";//
    public static final String ee_37 = "[衰]";//
    public static final String ee_38 = "[骷髅]";//
    public static final String ee_39 = "[敲打]";//
    public static final String ee_40 = "[再见]";//
    public static final String ee_41 = "[擦汗]";//
    public static final String ee_42 = "[抠鼻]";//
    public static final String ee_43 = "[鼓掌]";//
    public static final String ee_44 = "[糗大了]";//
    public static final String ee_45 = "[坏]";//
    public static final String ee_46 = "[左哼哼]";//
    public static final String ee_47 = "[右哼哼]";//
    public static final String ee_48 = "[哈欠]";//
    public static final String ee_49 = "[鄙视]";//
    public static final String ee_50 = "[委屈]";//
    public static final String ee_51 = "[快哭了]";//
    public static final String ee_52 = "[阴险]";//
    public static final String ee_53 = "[亲亲]";//
    public static final String ee_54 = "[吓]";//
    public static final String ee_55 = "[可怜]";//
    public static final String ee_56 = "[菜刀]";//
    public static final String ee_57 = "[西瓜]";//
    public static final String ee_58 = "[啤酒]";//
    public static final String ee_59 = "[篮球]";//
    public static final String ee_60 = "[兵乓]";//
    public static final String ee_61 = "[咖啡]";//
    public static final String ee_62 = "[饭]";//
    public static final String ee_63 = "[猪头]";//
    public static final String ee_64 = "[玫瑰]";//
    public static final String ee_65 = "[凋谢]";//
    public static final String ee_66 = "[嘴唇]";//
    public static final String ee_67 = "[爱心]";//
    public static final String ee_68 = "[心碎]";//
    public static final String ee_69 = "[蛋糕]";//
    public static final String ee_70 = "[闪电]";//
    public static final String ee_71 = "[炸弹]";//
    public static final String ee_72 = "[刀]";//
    public static final String ee_73 = "[足球]";//
    public static final String ee_74 = "[瓢虫]";//
    public static final String ee_75 = "[便便]";//
    public static final String ee_76 = "[月亮]";//
    public static final String ee_77 = "[太阳]";//
    public static final String ee_78 = "[礼物]";//
    public static final String ee_79 = "[拥抱]";//
    public static final String ee_80 = "[强]";//
    public static final String ee_81 = "[弱]";//
    public static final String ee_82 = "[握手]";//
    public static final String ee_83 = "[胜利]";//
    public static final String ee_84 = "[抱拳]";//
    public static final String ee_85 = "[勾引]";//
    public static final String ee_86 = "[拳头]";//
    public static final String ee_87 = "[差劲]";//
    public static final String ee_88 = "[爱你]";//
    public static final String ee_89 = "[NO]";//
    public static final String ee_90 = "[OK]";//

    private static final Factory spannableFactory = Factory
            .getInstance();

    private static final Map<Pattern, Integer> emoticons = new HashMap<Pattern, Integer>();

    static {

        addPattern(emoticons, ee_1, R.drawable.ee_1);
        addPattern(emoticons, ee_2, R.drawable.ee_2);
        addPattern(emoticons, ee_3, R.drawable.ee_3);
        addPattern(emoticons, ee_4, R.drawable.ee_4);
        addPattern(emoticons, ee_5, R.drawable.ee_5);
        addPattern(emoticons, ee_6, R.drawable.ee_6);
        addPattern(emoticons, ee_7, R.drawable.ee_7);
        addPattern(emoticons, ee_8, R.drawable.ee_8);
        addPattern(emoticons, ee_9, R.drawable.ee_9);
        addPattern(emoticons, ee_10, R.drawable.ee_10);
        addPattern(emoticons, ee_11, R.drawable.ee_11);
        addPattern(emoticons, ee_12, R.drawable.ee_12);
        addPattern(emoticons, ee_13, R.drawable.ee_13);
        addPattern(emoticons, ee_14, R.drawable.ee_14);
        addPattern(emoticons, ee_15, R.drawable.ee_15);
        addPattern(emoticons, ee_16, R.drawable.ee_16);
        addPattern(emoticons, ee_17, R.drawable.ee_17);
        addPattern(emoticons, ee_18, R.drawable.ee_18);
        addPattern(emoticons, ee_19, R.drawable.ee_19);
        addPattern(emoticons, ee_20, R.drawable.ee_20);
        addPattern(emoticons, ee_21, R.drawable.ee_21);
        addPattern(emoticons, ee_22, R.drawable.ee_22);
        addPattern(emoticons, ee_23, R.drawable.ee_23);
        addPattern(emoticons, ee_24, R.drawable.ee_24);
        addPattern(emoticons, ee_25, R.drawable.ee_25);
        addPattern(emoticons, ee_26, R.drawable.ee_26);
        addPattern(emoticons, ee_27, R.drawable.ee_27);
        addPattern(emoticons, ee_28, R.drawable.ee_28);
        addPattern(emoticons, ee_29, R.drawable.ee_29);
        addPattern(emoticons, ee_30, R.drawable.ee_30);
        addPattern(emoticons, ee_31, R.drawable.ee_31);
        addPattern(emoticons, ee_32, R.drawable.ee_32);
        addPattern(emoticons, ee_33, R.drawable.ee_33);
        addPattern(emoticons, ee_34, R.drawable.ee_34);
        addPattern(emoticons, ee_35, R.drawable.ee_35);
        addPattern(emoticons, ee_36, R.drawable.ee_36);
        addPattern(emoticons, ee_37, R.drawable.ee_37);
        addPattern(emoticons, ee_38, R.drawable.ee_38);
        addPattern(emoticons, ee_39, R.drawable.ee_39);
        addPattern(emoticons, ee_40, R.drawable.ee_40);
        addPattern(emoticons, ee_41, R.drawable.ee_41);
        addPattern(emoticons, ee_42, R.drawable.ee_42);
        addPattern(emoticons, ee_43, R.drawable.ee_43);
        addPattern(emoticons, ee_44, R.drawable.ee_44);
        addPattern(emoticons, ee_45, R.drawable.ee_45);
        addPattern(emoticons, ee_46, R.drawable.ee_46);
        addPattern(emoticons, ee_47, R.drawable.ee_47);
        addPattern(emoticons, ee_48, R.drawable.ee_48);
        addPattern(emoticons, ee_49, R.drawable.ee_49);
        addPattern(emoticons, ee_50, R.drawable.ee_50);
        addPattern(emoticons, ee_51, R.drawable.ee_51);
        addPattern(emoticons, ee_52, R.drawable.ee_52);
        addPattern(emoticons, ee_53, R.drawable.ee_53);
        addPattern(emoticons, ee_54, R.drawable.ee_54);
        addPattern(emoticons, ee_55, R.drawable.ee_55);
        addPattern(emoticons, ee_56, R.drawable.ee_56);
        addPattern(emoticons, ee_57, R.drawable.ee_57);
        addPattern(emoticons, ee_58, R.drawable.ee_58);
        addPattern(emoticons, ee_59, R.drawable.ee_59);
        addPattern(emoticons, ee_60, R.drawable.ee_60);
        addPattern(emoticons, ee_61, R.drawable.ee_61);
        addPattern(emoticons, ee_62, R.drawable.ee_62);
        addPattern(emoticons, ee_63, R.drawable.ee_63);
        addPattern(emoticons, ee_64, R.drawable.ee_64);
        addPattern(emoticons, ee_65, R.drawable.ee_65);
        addPattern(emoticons, ee_66, R.drawable.ee_66);
        addPattern(emoticons, ee_67, R.drawable.ee_67);
        addPattern(emoticons, ee_68, R.drawable.ee_68);
        addPattern(emoticons, ee_69, R.drawable.ee_69);
        addPattern(emoticons, ee_70, R.drawable.ee_70);
        addPattern(emoticons, ee_71, R.drawable.ee_71);
        addPattern(emoticons, ee_72, R.drawable.ee_72);
        addPattern(emoticons, ee_73, R.drawable.ee_73);
        addPattern(emoticons, ee_74, R.drawable.ee_74);
        addPattern(emoticons, ee_75, R.drawable.ee_75);
        addPattern(emoticons, ee_76, R.drawable.ee_76);
        addPattern(emoticons, ee_77, R.drawable.ee_77);
        addPattern(emoticons, ee_78, R.drawable.ee_78);
        addPattern(emoticons, ee_79, R.drawable.ee_79);
        addPattern(emoticons, ee_80, R.drawable.ee_80);
        addPattern(emoticons, ee_81, R.drawable.ee_81);
        addPattern(emoticons, ee_82, R.drawable.ee_82);
        addPattern(emoticons, ee_83, R.drawable.ee_83);
        addPattern(emoticons, ee_84, R.drawable.ee_84);
        addPattern(emoticons, ee_85, R.drawable.ee_85);
        addPattern(emoticons, ee_86, R.drawable.ee_86);
        addPattern(emoticons, ee_87, R.drawable.ee_87);
        addPattern(emoticons, ee_88, R.drawable.ee_88);
        addPattern(emoticons, ee_89, R.drawable.ee_89);
        addPattern(emoticons, ee_90, R.drawable.ee_90);
    }

    private static void addPattern(Map<Pattern, Integer> map, String smile,
                                   int resource) {
        map.put(Pattern.compile(Pattern.quote(smile)), resource);
    }

    /**
     * replace existing spannable with smiles
     * @param context
     * @param spannable
     * @return
     */
    public static boolean addSmiles(Context context, Spannable spannable) {
        boolean hasChanges = false;
        for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
            Matcher matcher = entry.getKey().matcher(spannable);
            while (matcher.find()) {
                boolean set = true;
                for (ImageSpan span : spannable.getSpans(matcher.start(),
                        matcher.end(), ImageSpan.class))
                    if (spannable.getSpanStart(span) >= matcher.start()
                            && spannable.getSpanEnd(span) <= matcher.end())
                        spannable.removeSpan(span);
                    else {
                        set = false;
                        break;
                    }
                if (set) {
                    hasChanges = true;
                    spannable.setSpan(new ImageSpan(context, entry.getValue()),
                            matcher.start(), matcher.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return hasChanges;
    }

    public static Spannable getSmiledText(Context context, CharSequence text) {
        Spannable spannable = spannableFactory.newSpannable(text);
        addSmiles(context, spannable);
        return spannable;
    }

    public static boolean containsKey(String key) {
        boolean b = false;
        for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
            Matcher matcher = entry.getKey().matcher(key);
            if (matcher.find()) {
                b = true;
                break;
            }
        }

        return b;
    }


}
