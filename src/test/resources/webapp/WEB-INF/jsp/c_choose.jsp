<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:choose>
<c:when test="${change &gt; 0}">
<span class="change positive">% +<c:out value="${change}"/></span>
</c:when>
<c:otherwise>
<span class="change negative">% <c:out value="${change}"/></span>
</c:otherwise>
</c:choose>
