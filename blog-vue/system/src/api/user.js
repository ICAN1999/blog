import request from "@/utlis/request";

export function getUsers(params) {
  return request({
    url: "/admin/users",
    method: "get",
    params,
  });
}

export function changeUserStatus(data) {
  return request({
    url: "/admin/changeStatus",
    method: "put",
    data,
  });
}

export function getOnlineUser(keyword) {
  return request({
    url: "/admin/online",
    method: "get",
    params: {
      keyword,
    },
  });
}

export function forceLogout(userId) {
  return request({
    url: `/admin/online/${userId}`,
    method: "delete",
  });
}

export function updatePassword(data) {
  return request({
    url: "/admin/password",
    method: "put",
    data,
  });
}

export function getUserZone(userType) {
  return request({
    url: "/admin/users/zone",
    method: "get",
    params: {
      userType,
    },
  });
}

export function updateAdminInfo(data) {
  return request({
    url: "/admin/info",
    method: "put",
    data,
  });
}
