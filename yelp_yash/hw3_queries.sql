select b.business_id, a.a_value, m.business_id from business b, attributes a, main_category m
where b.business_id = m.business_id AND  category = 'Home Services' OR category = 'Dentists' 

select b.business_id, a.a_value, m.business_id from business b, attributes a, main_category m
where b.business_id = m.business_id AND category = 'Restaurants' OR category = 'Restaurants'

SELECT DISTINCT CATEGORY FROM SUB_CATEGORY WHERE  CATEGORY = 'Wine Bars';

SELECT BUSINESS_ID FROM BUSINESS WHERE BUSINESS_ID IN
(SELECT main_category.business_id FROM MAIN_CATEGORY WHERE CATEGORY LIKE '%Department Stores%') 
INTERSECT (SELECT sub_category.business_id FROM SUB_CATEGORY WHERE category LIKE '%Sporting Goods%')


select business_id,name,city,state,stars from business join main_category on business_id = business_id where category =""

SELECT DISTINCT s.CATEGORY FROM SUB_CATEGORY s JOIN MAIN_CATEGORY c  ON c.BUSINESS_ID = s.BUSINESS_ID WHERE c.category ="" OR c.category=""